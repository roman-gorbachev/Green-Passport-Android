package com.smartcity.greenpassport.feature.map.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.feature.map.domain.GetMapPointsUseCase
import com.smartcity.greenpassport.feature.map.domain.ObserveSavedMapPointIdsUseCase
import com.smartcity.greenpassport.feature.map.domain.ToggleSavedMapPointUseCase
import com.smartcity.greenpassport.feature.map.presentation.state.MapUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMapPoints: GetMapPointsUseCase,
    private val toggleSavedMapPoint: ToggleSavedMapPointUseCase,
    observeSavedMapPointIds: ObserveSavedMapPointIdsUseCase,
) : ViewModel() {

    private val refreshRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val filters = MutableStateFlow(MapFilters())

    val uiState = observeMapUiState(observeSavedMapPointIds()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        MapUiState(),
    )

    fun refresh() {
        refreshRequests.tryEmit(Unit)
    }

    fun onTypeSelected(type: MapPointType?) {
        filters.update { it.copy(selectedType = type) }
    }

    fun onSearchQueryChange(query: String) {
        filters.update { it.copy(searchQuery = query) }
    }

    fun onPointSelected(pointId: String?) {
        filters.update { it.copy(selectedPointId = pointId) }
    }

    fun onToggleSaved(pointId: String) {
        val savedIds = uiState.value.savedPointIds
        viewModelScope.launch {
            runCatching { toggleSavedMapPoint(pointId, savedIds) }
                .onFailure { error -> Log.w(TAG, "Failed to toggle saved point", error) }
        }
    }

    private fun observeMapUiState(savedIds: Flow<Set<String>>): Flow<MapUiState> {
        return combine(
            observePoints(),
            savedIds.catch { emit(emptySet()) }.onStart { emit(emptySet()) },
            filters,
        ) { points, saved, currentFilters ->
            MapUiState(
                points = points.points.orEmpty(),
                savedPointIds = saved,
                selectedType = currentFilters.selectedType,
                searchQuery = currentFilters.searchQuery,
                selectedPointId = currentFilters.selectedPointId,
                isLoading = points.isLoading,
                hasError = points.hasError,
            )
        }
    }

    private fun observePoints(): Flow<PointsLoad> {
        return refreshRequests
            .onStart { emit(Unit) }
            .flatMapLatest {
                flow {
                    emit(PointsLoad(isLoading = true))
                    val points = runCatching { getMapPoints() }
                    emit(PointsLoad(points = points.getOrNull(), hasError = points.isFailure))
                }
            }
    }

    private data class MapFilters(
        val selectedType: MapPointType? = null,
        val searchQuery: String = "",
        val selectedPointId: String? = null,
    )

    private data class PointsLoad(
        val points: List<MapPoint>? = null,
        val isLoading: Boolean = false,
        val hasError: Boolean = false,
    )

    companion object {
        private const val TAG = "MapViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
