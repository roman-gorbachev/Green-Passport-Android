package com.smartcity.greenpassport.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.feature.profile.domain.GetBookmarkedTipsUseCase
import com.smartcity.greenpassport.feature.profile.domain.ObserveProfileSessionUseCase
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
class BookmarksViewModel @Inject constructor(
    private val getBookmarkedTips: GetBookmarkedTipsUseCase,
    observeSession: ObserveProfileSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSession().collectLatest { session ->
                currentUserId = session?.userId
                if (session == null) {
                    _uiState.update { it.copy(tips = emptyList(), isLoading = false) }
                    return@collectLatest
                }
                loadBookmarks(session.userId)
            }
        }
    }

    fun retry() {
        val userId = currentUserId ?: return
        viewModelScope.launch { loadBookmarks(userId) }
    }

    private suspend fun loadBookmarks(userId: String) {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        try {
            val tips = getBookmarkedTips(userId)
            _uiState.update { it.copy(tips = tips, isLoading = false) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _uiState.update { it.copy(isLoading = false, hasError = true) }
        }
    }
}
