package com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.ecotips.domain.GetEcoTipsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.GetReadTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.MarkTipReadUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveBookmarkedTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveEcoTipsSessionUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ToggleTipBookmarkUseCase
import com.smartcity.greenpassport.feature.ecotips.presentation.state.EcoTipDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EcoTipDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEcoTips: GetEcoTipsUseCase,
    private val getReadTipIds: GetReadTipIdsUseCase,
    private val markTipRead: MarkTipReadUseCase,
    private val observeBookmarkedTipIds: ObserveBookmarkedTipIdsUseCase,
    private val toggleTipBookmark: ToggleTipBookmarkUseCase,
    observeSession: ObserveEcoTipsSessionUseCase,
) : ViewModel() {

    private val tipId: String = checkNotNull(savedStateHandle["tipId"])
    private var currentUserId: String? = null

    private val _uiState = MutableStateFlow(EcoTipDetailUiState())
    val uiState: StateFlow<EcoTipDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                loadTip()
            }
        }
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                if (session == null) {
                    _uiState.update { it.copy(isBookmarked = false) }
                    return@collectLatest
                }
                observeBookmarkedTipIds(session.userId)
                    .catch { error -> Log.w(TAG, "Failed to observe bookmarks", error) }
                    .collect { bookmarkedIds -> _uiState.update { it.copy(isBookmarked = tipId in bookmarkedIds) } }
            }
        }
    }

    fun onToggleBookmark() {
        val userId = currentUserId ?: return
        val isBookmarked = !_uiState.value.isBookmarked
        _uiState.update { it.copy(isBookmarked = isBookmarked) }
        viewModelScope.launch {
            runCatching { toggleTipBookmark(userId, tipId, isBookmarked) }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    Log.w(TAG, "Failed to toggle bookmark", error)
                    _uiState.update { it.copy(isBookmarked = !isBookmarked) }
                }
        }
    }

    fun retry() {
        viewModelScope.launch { loadTip() }
    }

    private suspend fun loadTip() {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        runCatching {
            val tip = getEcoTips().firstOrNull { it.id == tipId }
            val isRead = currentUserId?.let { getReadTipIds(it) }?.contains(tipId) ?: false
            _uiState.update { it.copy(tip = tip, isRead = isRead, isLoading = false) }
        }.onFailure {
            _uiState.update { it.copy(isLoading = false, hasError = true) }
        }
    }

    fun onMarkAsRead() {
        val state = _uiState.value
        val tip = state.tip ?: return
        if (currentUserId == null) return
        if (state.isRead || state.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            runCatching {
                markTipRead(tip)
                _uiState.update { it.copy(isSubmitting = false, isRead = true) }
            }.onFailure {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    companion object {
        private const val TAG = "EcoTipDetailViewModel"
    }
}
