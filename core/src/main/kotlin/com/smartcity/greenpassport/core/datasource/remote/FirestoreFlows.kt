package com.smartcity.greenpassport.core.datasource.remote

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

private const val EMPTY_CACHE_GRACE_MILLIS = 5_000L

fun Query.cacheFirstSnapshots(): Flow<QuerySnapshot> = callbackFlow {
    var emptyCacheFallback: Job? = null
    val registration = addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }
        if (snapshot == null) return@addSnapshotListener
        emptyCacheFallback?.cancel()
        if (snapshot.metadata.isFromCache && snapshot.isEmpty) {
            emptyCacheFallback = launch {
                delay(EMPTY_CACHE_GRACE_MILLIS)
                trySend(snapshot)
            }
        } else {
            trySend(snapshot)
        }
    }
    awaitClose { registration.remove() }
}

fun DocumentReference.cacheFirstSnapshots(): Flow<DocumentSnapshot> = callbackFlow {
    var missingCacheFallback: Job? = null
    val registration = addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }
        if (snapshot == null) return@addSnapshotListener
        missingCacheFallback?.cancel()
        if (snapshot.metadata.isFromCache && !snapshot.exists()) {
            missingCacheFallback = launch {
                delay(EMPTY_CACHE_GRACE_MILLIS)
                trySend(snapshot)
            }
        } else {
            trySend(snapshot)
        }
    }
    awaitClose { registration.remove() }
}
