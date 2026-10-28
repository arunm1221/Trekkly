package com.example.trekkly.data.remote

import android.util.Log
import com.example.trekkly.data.mapper.TrekDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class TrekRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun observeTreks(): Flow<List<TrekDto>> = callbackFlow {
        val registration = firestore.collection("treks")
            .addSnapshotListener { snapshots, exception ->
                if (exception!=null){
                    Log.e(TAG, "treks listener failed", exception)
                    close(exception)
                    return@addSnapshotListener
                }
                if (snapshots == null) return@addSnapshotListener

                Log.d(
                    TAG,
                    "snapshot: ${snapshots.size()} docs, fromCache=${snapshots.metadata.isFromCache}"
                )

                // An offline cold cache reports an empty collection with no error, which is
                // indistinguishable from a genuinely empty catalogue. Ignore it and wait for
                // the server snapshot that follows once connectivity returns.
                if (snapshots.isEmpty && snapshots.metadata.isFromCache) {
                    Log.w(TAG, "empty snapshot served from cache - offline? ignoring")
                    return@addSnapshotListener
                }

                trySend(snapshots.toObjects(TrekDto::class.java))
            }
        awaitClose {
            registration.remove()
        }
    }

    private companion object {
        const val TAG = "TrekSync"
    }
}