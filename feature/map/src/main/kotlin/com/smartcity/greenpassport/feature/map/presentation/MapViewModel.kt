package com.smartcity.greenpassport.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.MapPointType
import com.smartcity.greenpassport.feature.map.domain.GetMapPointsUseCase
import com.smartcity.greenpassport.feature.map.domain.ObserveSavedMapPointIdsUseCase
import com.smartcity.greenpassport.feature.map.domain.ToggleSavedMapPointUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getMapPoints: GetMapPointsUseCase,
    private val toggleSavedMapPoint: ToggleSavedMapPointUseCase,
    observeSavedMapPointIds: ObserveSavedMapPointIdsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val points = getMapPoints()
            _uiState.update { it.copy(points = points, isLoading = false) }
        }

        viewModelScope.launch {
            observeSavedMapPointIds().collectLatest { savedIds ->
                _uiState.update { it.copy(savedPointIds = savedIds) }
            }
        }
    }

    fun onTypeSelected(type: MapPointType?) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onToggleSaved(pointId: String) {
        viewModelScope.launch {
            toggleSavedMapPoint(pointId, _uiState.value.savedPointIds)
        }
    }
}
