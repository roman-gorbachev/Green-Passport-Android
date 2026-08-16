package com.smartcity.greenpassport.feature.map.domain

import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val KEY_SAVED_MAP_POINTS = "saved_map_point_ids"
private const val SAVED_IDS_SEPARATOR = ","

class GetMapPointsUseCase @Inject constructor(
    private val mapPointsRepository: MapPointsRepository,
) {
    suspend operator fun invoke(): List<MapPoint> = mapPointsRepository.getPoints()
}

class ObserveSavedMapPointIdsUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    operator fun invoke(): Flow<Set<String>> =
        settingsStore.getString(KEY_SAVED_MAP_POINTS, "").map { raw -> raw.toSavedIdsSet() }
}

class ToggleSavedMapPointUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    suspend operator fun invoke(pointId: String, currentSavedIds: Set<String>) {
        val updated = if (pointId in currentSavedIds) currentSavedIds - pointId else currentSavedIds + pointId
        settingsStore.setString(KEY_SAVED_MAP_POINTS, updated.joinToString(SAVED_IDS_SEPARATOR))
    }
}

private fun String?.toSavedIdsSet(): Set<String> =
    this.orEmpty().split(SAVED_IDS_SEPARATOR).filter { it.isNotBlank() }.toSet()
