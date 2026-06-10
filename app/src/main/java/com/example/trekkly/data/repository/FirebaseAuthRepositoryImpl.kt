package com.example.trekkly.data.repository

import android.app.Activity
import com.example.trekkly.domain.repository.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    companion object{
        const val AUTO_VERIFIED_MARKER = "auto-verified"
    }
    private var resendToken : PhoneAuthProvider.ForceResendingToken? = null
    private var autoVerifiedCredential: PhoneAuthCredential? = null

    /**
     * Builds Firebase callbacks that bridge the callback API to coroutine continuations.
     * [onSent] fires with a verificationId when the SMS is sent, or with [AUTO_VERIFIED_MARKER]
     * when Android auto-reads the SMS and resolves the credential without user input.
     */
    private fun buildCallbacks(
        onSent:(verificationId:String) ->Unit,
        onFailed:(Exception)->Unit
    ) = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            autoVerifiedCredential = credential
            onSent(AUTO_VERIFIED_MARKER)

        }

        override fun onVerificationFailed(e: FirebaseException) {
            onFailed(e)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)
            resendToken = token
            onSent(verificationId)
        }

    }


    override suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<String> =
        suspendCancellableCoroutine { continuation ->
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(buildCallbacks(
                    onSent = { verificationId ->
                        if (continuation.isActive) continuation.resume(Result.success(verificationId))
                    },
                    onFailed = { e ->
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
                ))
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }


    override suspend fun verifyOtp(
        verificationId: String,
        otp: String
    ): Result<String> =
        suspendCancellableCoroutine { continuation ->
            val credential = if (verificationId == AUTO_VERIFIED_MARKER) {
                // Auto-retrieval path — use the pre-stored credential directly
                autoVerifiedCredential ?: run {
                    continuation.resume(
                        Result.failure(IllegalStateException("Auto-verified credential missing. Please request a new code."))
                    )
                    return@suspendCancellableCoroutine
                }
            } else {
                // Manual entry path — build credential from verificationId + typed OTP
                PhoneAuthProvider.getCredential(verificationId, otp)
            }

            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid
                    if (uid != null) {
                        continuation.resume(Result.success(uid))
                    } else {
                        continuation.resume(
                            Result.failure(IllegalStateException("Sign-in succeeded but uid was null."))
                        )
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
        }

    override fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    override suspend fun signOut() {
        autoVerifiedCredential = null
        resendToken = null
        firebaseAuth.signOut()
    }

    override suspend fun resendOtp(phoneNumber: String, activity: Activity): Result<String> =
        suspendCancellableCoroutine { continuation ->
            val builder = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(buildCallbacks(
                    onSent = { verificationId ->
                        if (continuation.isActive) continuation.resume(Result.success(verificationId))
                    },
                    onFailed = { e ->
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
                ))

            resendToken?.let { builder.setForceResendingToken(it) }
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }

}