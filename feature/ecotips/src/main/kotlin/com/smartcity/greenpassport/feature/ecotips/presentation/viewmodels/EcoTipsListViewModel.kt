package com.smartcity.greenpassport.feature.ecotips.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.feature.ecotips.domain.GetEcoTipsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.GetReadTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveBookmarkedTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveEcoTipsSessionUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ToggleTipBookmarkUseCase
import com.smartcity.greenpassport.feature.ecotips.presentation.state.EcoTipsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EcoTipsListViewModel @Inject constructor(
    private val getEcoTips: GetEcoTipsUseCase,
    private val getReadTipIds: GetReadTipIdsUseCase,
    private val observeBookmarkedTipIds: ObserveBookmarkedTipIdsUseCase,
    private val toggleTipBookmark: ToggleTipBookmarkUseCase,
    observeSession: ObserveEcoTipsSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EcoTipsListUiState())
    val uiState: StateFlow<EcoTipsListUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                refresh()
                if (session != null) {
                    observeBookmarkedTipIds(session.userId).collectLatest { bookmarkedIds ->
                        _uiState.update { it.copy(bookmarkedTipIds = bookmarkedIds) }
                    }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            runCatching {
                val userId = currentUserId
                val tips = getEcoTips()
                val readIds = userId?.let { getReadTipIds(it) } ?: emptySet()
                _uiState.update { it.copy(tips = tips, readTipIds = readIds, isLoading = false) }
            }.onFailure { error ->
                Log.e("EcoTipsViewModel::refresh()", error.message.orEmpty())
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun onCategorySelected(category: EcoTipCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onToggleBookmark(tipId: String) {
        val userId = currentUserId ?: return
        val isBookmarked = _uiState.value.bookmarkedTipIds.contains(tipId)
        viewModelScope.launch {
            runCatching {
                toggleTipBookmark(userId, tipId, !isBookmarked)
            }.onFailure { error ->
                Log.e("EcoTipsViewModel::onToggleBookmark()", error.message.orEmpty())
            }
        }
    }
}
