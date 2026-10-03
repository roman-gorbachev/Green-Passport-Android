package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val KEY_SAVED_MAP_POINTS = "saved_map_point_ids"
private const val SAVED_IDS_SEPARATOR = ","

class GetSavedPlacesUseCase @Inject constructor(
    private val mapPointsRepository: MapPointsRepository,
    private val settingsStore: LocalSettingsStore,
) {
    suspend operator fun invoke(): List<MapPoint> {
        val savedIds = settingsStore.getString(KEY_SAVED_MAP_POINTS, "").first().toSavedIdsSet()
        if (savedIds.isEmpty()) return emptyList()
        return mapPointsRepository.getPoints().filter { it.isActive && savedIds.contains(it.id) }
    }
}

private fun String?.toSavedIdsSet(): Set<String> =
    this.orEmpty().split(SAVED_IDS_SEPARATOR).filter { it.isNotBlank() }.toSet()
