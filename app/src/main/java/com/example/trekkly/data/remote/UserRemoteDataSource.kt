package com.example.trekkly.data.remote

import com.example.trekkly.data.mapper.UserFields
import com.example.trekkly.data.mapper.toFirestoreMap
import com.example.trekkly.data.mapper.toUserOrNull
import com.example.trekkly.domain.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Reads and writes user profile documents at users/{uid}. */
class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val users = firestore.collection("users")

    /**
     * Writes the profile with merge semantics so a repeat sign-up on the same
     * phone number refreshes the name without wiping unrelated fields.
     */
    suspend fun upsertUser(user: User, isNew: Boolean) {
        val data = user.toFirestoreMap().toMutableMap()
        data[UserFields.UPDATED_AT] = FieldValue.serverTimestamp()
        if (isNew) data[UserFields.CREATED_AT] = FieldValue.serverTimestamp()
        users.document(user.uid).set(data, SetOptions.merge()).awaitResult()
    }

    suspend fun getUser(uid: String): User? =
        users.document(uid).get().awaitResult().toUserOrNull()

    suspend fun findByPhone(phoneNumber: String): User? =
        users.whereEqualTo(UserFields.PHONE_NUMBER, phoneNumber)
            .limit(1)
            .get()
            .awaitResult()
            .documents
            .firstOrNull()
            ?.toUserOrNull()

    /** Bridges a Firebase Task to a coroutine, matching the auth repo's style. */
    private suspend fun <T> Task<T>.awaitResult(): T =
        suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { if (continuation.isActive) continuation.resume(it) }
            addOnFailureListener { e -> if (continuation.isActive) continuation.resumeWithException(e) }
        }
}
