package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

private const val APPS_COLLECTION = "apps"
private const val APP_DOCUMENT = "greenpassport"
private const val USERS_COLLECTION = "users"

object FirestoreCollections {
    fun appRoot(firestore: FirebaseFirestore): DocumentReference =
        firestore.collection(APPS_COLLECTION).document(APP_DOCUMENT)

    fun users(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(USERS_COLLECTION)
}
