package com.smartcity.greenpassport.core.database

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.core.model.MapPointsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

private const val FIELD_NAME = "name"
private const val FIELD_TYPE = "type"
private const val FIELD_ADDRESS = "address"
private const val FIELD_CITY = "city"
private const val FIELD_LATITUDE = "latitude"
private const val FIELD_LONGITUDE = "longitude"

class FirestoreMapPointsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : MapPointsRepository {

    override suspend fun getPoints(): List<MapPoint> {
        val snapshot = FirestoreCollections.mapPoints(firestore).get().await()
        return snapshot.documents.mapNotNull { it.toMapPoint() }
    }
}

private fun DocumentSnapshot.toMapPoint(): MapPoint? {
    val name = getString(FIELD_NAME) ?: return null
    val type = getString(FIELD_TYPE)?.let { name ->
        runCatching { MapPointType.valueOf(name) }.getOrNull()
    } ?: return null
    val address = getString(FIELD_ADDRESS) ?: return null
    val city = getString(FIELD_CITY) ?: return null
    val latitude = getDouble(FIELD_LATITUDE) ?: return null
    val longitude = getDouble(FIELD_LONGITUDE) ?: return null

    return MapPoint(
        id = id,
        name = name,
        type = type,
        address = address,
        city = city,
        latitude = latitude,
        longitude = longitude,
    )
}
