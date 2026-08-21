package com.smartcity.greenpassport.feature.map.domain

import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val KEY_SAVED_MAP_POINTS = "saved_map_point_ids"
private const val SAVED_IDS_SEPARATOR = ","

class ObserveSavedMapPointIdsUseCase @Inject constructor(
    private val settingsStore: LocalSettingsStore,
) {
    operator fun invoke(): Flow<Set<String>> =
        settingsStore.getString(KEY_SAVED_MAP_POINTS, "").map { raw -> raw.toSavedIdsSet() }
}

private fun String?.toSavedIdsSet(): Set<String> =
    this.orEmpty().split(SAVED_IDS_SEPARATOR).filter { it.isNotBlank() }.toSet()
