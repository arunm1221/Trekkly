package com.example.trekkly.data.remote

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
                    close(exception)
                    return@addSnapshotListener
                }
                if (snapshots!=null){
                    trySend(snapshots.toObjects(TrekDto::class.java))
                }
            }
        awaitClose {
            registration.remove()
        }
    }
}