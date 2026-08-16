package com.smartcity.greenpassport.feature.ecotips.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.ecotips.domain.GetEcoTipsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.GetReadTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.MarkTipReadUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveEcoTipsSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EcoTipDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getEcoTips: GetEcoTipsUseCase,
    private val getReadTipIds: GetReadTipIdsUseCase,
    private val markTipRead: MarkTipReadUseCase,
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
    }

    fun retry() {
        viewModelScope.launch { loadTip() }
    }

    private suspend fun loadTip() {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        try {
            val tip = getEcoTips().firstOrNull { it.id == tipId }
            val isRead = currentUserId?.let { getReadTipIds(it) }?.contains(tipId) ?: false
            _uiState.update { it.copy(tip = tip, isRead = isRead, isLoading = false) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _uiState.update { it.copy(isLoading = false, hasError = true) }
        }
    }

    fun onMarkAsRead() {
        val state = _uiState.value
        val tip = state.tip ?: return
        val userId = currentUserId ?: return
        if (state.isRead || state.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                markTipRead(userId, tip)
                _uiState.update { it.copy(isSubmitting = false, isRead = true) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
