package com.smartcity.greenpassport.feature.profile.presentation.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.profile.domain.GetFavoriteTasksUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveProfileSessionUseCase
import com.smartcity.greenpassport.feature.profile.domain.RemoveFavoriteTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteTasks: GetFavoriteTasksUseCase,
    private val removeFavoriteTask: RemoveFavoriteTaskUseCase,
    observeSession: ObserveProfileSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                if (session == null) {
                    _uiState.update { it.copy(tasks = emptyList(), isLoading = false) }
                    return@collectLatest
                }
                loadFavorites(session.userId)
            }
        }
    }

    fun onRemoveTask(taskId: String) {
        val userId = currentUserId ?: return
        val removed = _uiState.value.tasks
        _uiState.update { state -> state.copy(tasks = state.tasks.filterNot { it.id == taskId }) }
        viewModelScope.launch {
            try {
                removeFavoriteTask(userId, taskId)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(TAG, "Failed to remove from favorites", error)
                _uiState.update { it.copy(tasks = removed) }
            }
        }
    }

    fun retry() {
        val userId = currentUserId ?: return
        viewModelScope.launch { loadFavorites(userId) }
    }

    private suspend fun loadFavorites(userId: String) {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        try {
            val tasks = getFavoriteTasks(userId)
            _uiState.update { it.copy(tasks = tasks, isLoading = false) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _uiState.update { it.copy(isLoading = false, hasError = true) }
        }
    }

    companion object {
        private const val TAG = "FavoritesViewModel"
    }
}
