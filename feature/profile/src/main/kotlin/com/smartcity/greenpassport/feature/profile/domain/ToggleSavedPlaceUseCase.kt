package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val KEY_SAVED_MAP_POINTS = "saved_map_point_ids"
private const val SAVED_IDS_SEPARATOR = ","

class ToggleSavedPlaceUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    suspend operator fun invoke(pointId: String) {
        val currentIds = settingsStore.getString(KEY_SAVED_MAP_POINTS, "").first().toSavedIdsSet()
        val updated = if (pointId in currentIds) currentIds - pointId else currentIds + pointId
        settingsStore.setString(KEY_SAVED_MAP_POINTS, updated.joinToString(SAVED_IDS_SEPARATOR))
    }
}

private fun String?.toSavedIdsSet(): Set<String> =
    this.orEmpty().split(SAVED_IDS_SEPARATOR).filter { it.isNotBlank() }.toSet()
