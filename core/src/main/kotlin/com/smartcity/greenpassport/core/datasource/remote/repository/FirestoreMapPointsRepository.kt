package com.smartcity.greenpassport.core.datasource.remote.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smartcity.greenpassport.core.datasource.remote.FirestoreCollections
import com.smartcity.greenpassport.core.datasource.remote.cacheFirstSnapshots
import com.smartcity.greenpassport.core.datasource.remote.localizedText
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.core.model.MapPointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FIELD_NAME = "name"
private const val FIELD_TYPE = "type"
private const val FIELD_ADDRESS = "address"
private const val FIELD_NAMES = "names"
private const val FIELD_ADDRESSES = "addresses"
private const val FIELD_CITY = "city"
private const val FIELD_LATITUDE = "latitude"
private const val FIELD_LONGITUDE = "longitude"

class FirestoreMapPointsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : MapPointsRepository {

    override fun observePoints(): Flow<List<MapPoint>> =
        FirestoreCollections.mapPoints(firestore).cacheFirstSnapshots()
            .map { snapshot -> snapshot.documents.mapNotNull { it.toMapPoint() } }
            .distinctUntilChanged()

    override suspend fun getPoints(): List<MapPoint> = observePoints().first()
}

private fun DocumentSnapshot.toMapPoint(): MapPoint? {
    val name = localizedText(FIELD_NAME, FIELD_NAMES) ?: return null
    val type = getString(FIELD_TYPE)?.let { name ->
        runCatching { MapPointType.valueOf(name) }.getOrNull()
    } ?: return null
    val address = localizedText(FIELD_ADDRESS, FIELD_ADDRESSES) ?: return null
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
