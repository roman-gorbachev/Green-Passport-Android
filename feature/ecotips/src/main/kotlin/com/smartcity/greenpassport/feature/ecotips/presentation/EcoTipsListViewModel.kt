package com.smartcity.greenpassport.feature.ecotips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.EcoTipCategory
import com.smartcity.greenpassport.feature.ecotips.domain.GetEcoTipsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.GetReadTipIdsUseCase
import com.smartcity.greenpassport.feature.ecotips.domain.ObserveEcoTipsSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EcoTipsListViewModel @Inject constructor(
    private val getEcoTips: GetEcoTipsUseCase,
    private val getReadTipIds: GetReadTipIdsUseCase,
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
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = currentUserId
            val tips = getEcoTips()
            val readIds = userId?.let { getReadTipIds(it) } ?: emptySet()
            _uiState.update { it.copy(tips = tips, readTipIds = readIds, isLoading = false) }
        }
    }

    fun onCategorySelected(category: EcoTipCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}
