package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

private const val APPS_COLLECTION = "apps"
private const val APP_DOCUMENT = "greenpassport"
private const val USERS_COLLECTION = "users"
private const val TASKS_COLLECTION = "tasks"
private const val TASK_PROGRESS_COLLECTION = "taskProgress"
private const val SHOP_ITEMS_COLLECTION = "shopItems"
private const val PURCHASES_COLLECTION = "purchases"
private const val MAP_POINTS_COLLECTION = "mapPoints"
private const val EVENTS_COLLECTION = "events"
private const val EVENT_REGISTRATIONS_COLLECTION = "eventRegistrations"
private const val POSTS_COLLECTION = "posts"
private const val GROUPS_COLLECTION = "groups"
private const val CHATS_COLLECTION = "chats"
private const val MESSAGES_COLLECTION = "messages"

object FirestoreCollections {
    fun appRoot(firestore: FirebaseFirestore): DocumentReference =
        firestore.collection(APPS_COLLECTION).document(APP_DOCUMENT)

    fun users(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(USERS_COLLECTION)

    fun tasks(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(TASKS_COLLECTION)

    fun taskProgress(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(TASK_PROGRESS_COLLECTION)

    fun shopItems(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(SHOP_ITEMS_COLLECTION)

    fun purchases(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(PURCHASES_COLLECTION)

    fun mapPoints(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(MAP_POINTS_COLLECTION)

    fun events(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(EVENTS_COLLECTION)

    fun eventRegistrations(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(EVENT_REGISTRATIONS_COLLECTION)

    fun posts(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(POSTS_COLLECTION)

    fun groups(firestore: FirebaseFirestore): CollectionReference =
        appRoot(firestore).collection(GROUPS_COLLECTION)

    fun chatMessages(firestore: FirebaseFirestore, chatId: String): CollectionReference =
        appRoot(firestore).collection(CHATS_COLLECTION).document(chatId).collection(MESSAGES_COLLECTION)
}
