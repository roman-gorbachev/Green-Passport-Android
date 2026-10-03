package com.smartcity.greenpassport.feature.profile.presentation.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.MapPoint
import com.smartcity.greenpassport.feature.profile.domain.GetSavedPlacesUseCase
import com.smartcity.greenpassport.feature.profile.domain.ToggleSavedPlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPlacesViewModel @Inject constructor(
    private val getSavedPlaces: GetSavedPlacesUseCase,
    private val toggleSavedPlace: ToggleSavedPlaceUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedPlacesUiState())
    val uiState: StateFlow<SavedPlacesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { loadPlaces() }
    }

    fun retry() {
        refresh()
    }

    fun onToggleSaved(point: MapPoint) {
        viewModelScope.launch {
            toggleSavedPlace(point.id)
            loadPlaces()
        }
    }

    private suspend fun loadPlaces() {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        try {
            val places = getSavedPlaces()
            _uiState.update { it.copy(places = places, isLoading = false) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _uiState.update { it.copy(isLoading = false, hasError = true) }
        }
    }
}
