package com.smartcity.greenpassport.feature.map.domain

import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import javax.inject.Inject

private const val KEY_SAVED_MAP_POINTS = "saved_map_point_ids"
private const val SAVED_IDS_SEPARATOR = ","

class ToggleSavedMapPointUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    suspend operator fun invoke(pointId: String, currentSavedIds: Set<String>) {
        val updated = if (pointId in currentSavedIds) currentSavedIds - pointId else currentSavedIds + pointId
        settingsStore.setString(KEY_SAVED_MAP_POINTS, updated.joinToString(SAVED_IDS_SEPARATOR))
    }
}
