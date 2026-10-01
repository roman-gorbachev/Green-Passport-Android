package com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveBookmarkedTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveEcoTipsSessionUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveEcoTipsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveReadTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ToggleTipBookmarkUseCase
import com.smartcity.greenpassport.feature.ecotips.presentation.state.EcoTipsListUiState
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EcoTipsListViewModel @Inject constructor(
    private val observeEcoTips: ObserveEcoTipsUseCase,
    private val observeReadTipIds: ObserveReadTipIdsUseCase,
    private val observeBookmarkedTipIds: ObserveBookmarkedTipIdsUseCase,
    private val toggleTipBookmark: ToggleTipBookmarkUseCase,
    observeSession: ObserveEcoTipsSessionUseCase,
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<EcoTipCategory?>(null)

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private var currentUserId: String? = null

    val uiState = observeEcoTipsUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        EcoTipsListUiState(),
    )

    fun refresh() {
        retryRequests.tryEmit(Unit)
    }

    fun onCategorySelected(category: EcoTipCategory?) {
        selectedCategory.value = category
    }

    fun onToggleBookmark(tipId: String) {
        val userId = currentUserId ?: return
        val isBookmarked = uiState.value.bookmarkedTipIds.contains(tipId)
        viewModelScope.launch {
            runCatching { toggleTipBookmark(userId, tipId, !isBookmarked) }
                .onFailure { error -> Log.w(TAG, "Failed to toggle bookmark", error) }
        }
    }

    private fun observeEcoTipsUiState(sessions: Flow<AuthSession?>): Flow<EcoTipsListUiState> {
        val data = combine(sessions, retryRequests.onStart { emit(Unit) }) { session, _ -> session }
            .flatMapLatest { session ->
                currentUserId = session?.userId
                observeTipsData(session?.userId)
            }
        return combine(data, selectedCategory) { tipsData, category -> tipsData.copy(selectedCategory = category) }
    }

    private fun observeTipsData(userId: String?): Flow<EcoTipsListUiState> {
        val readIds = userId?.let { observeReadTipIds(it).catch { emit(emptySet()) } } ?: flowOf(emptySet())
        val bookmarkedIds = userId?.let { observeBookmarkedTipIds(it).catch { emit(emptySet()) } } ?: flowOf(emptySet())
        return combine(observeEcoTips(), readIds, bookmarkedIds) { tips, read, bookmarked ->
            EcoTipsListUiState(tips = tips, readTipIds = read, bookmarkedTipIds = bookmarked, isLoading = false)
        }
            .onStart { emit(EcoTipsListUiState()) }
            .catch { error ->
                Log.w(TAG, "Failed to observe tips", error)
                emit(EcoTipsListUiState(isLoading = false, hasError = true))
            }
    }

    companion object {
        private const val TAG = "EcoTipsListViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
