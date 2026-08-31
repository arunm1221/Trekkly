package com.example.trekkly.data.remote

import com.example.trekkly.data.mapper.UserTrekDto
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserTrekRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun collection(uid: String) =
        firestore.collection("users").document(uid).collection("treks")

    /** Live stream of the signed-in user's trek records. */
    fun observeUserTreks(uid: String): Flow<List<UserTrekDto>> = callbackFlow {
        val registration = collection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.toObjects(UserTrekDto::class.java))
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun scheduleTrek(uid: String, trekId: String, scheduleDate: Long) {
        val data = mapOf(
            "trekId" to trekId,
            "status" to "UPCOMING",
            "scheduleDate" to Timestamp(Date(scheduleDate)),
            "currentDay" to 0,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        collection(uid).document(trekId).set(data, SetOptions.merge()).awaitResult()
    }

    suspend fun startTrek(uid: String, trekId: String) {
        val data = mapOf(
            "status" to "ACTIVE",
            "startedAt" to FieldValue.serverTimestamp(),
            "currentDay" to 1,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        collection(uid).document(trekId).set(data, SetOptions.merge()).awaitResult()
    }

    suspend fun updateProgress(uid: String, trekId: String, currentDay: Int) {
        val data = mapOf(
            "currentDay" to currentDay,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        collection(uid).document(trekId).set(data, SetOptions.merge()).awaitResult()
    }

    suspend fun completeTrek(uid: String, trekId: String) {
        val data = mapOf(
            "status" to "COMPLETED",
            "completedAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        collection(uid).document(trekId).set(data, SetOptions.merge()).awaitResult()
    }

    suspend fun removeUserTrek(uid: String, trekId: String) {
        collection(uid).document(trekId).delete().awaitResult()
    }

    /** Bridges a Firebase Task to a coroutine, matching the auth repo's style. */
    private suspend fun <T> Task<T>.awaitResult(): T =
        suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { if (continuation.isActive) continuation.resume(it) }
            addOnFailureListener { e -> if (continuation.isActive) continuation.resumeWithException(e) }
        }
}
