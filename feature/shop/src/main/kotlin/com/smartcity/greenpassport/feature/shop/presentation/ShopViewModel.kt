package com.smartcity.greenpassport.feature.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.feature.shop.domain.GetPurchasesUseCase
import com.smartcity.greenpassport.feature.shop.domain.GetRewardsUseCase
import com.smartcity.greenpassport.feature.shop.domain.GetShopPointsBalanceUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveShopSessionUseCase
import com.smartcity.greenpassport.feature.shop.domain.PurchaseRewardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getRewards: GetRewardsUseCase,
    private val getPurchases: GetPurchasesUseCase,
    private val getPointsBalance: GetShopPointsBalanceUseCase,
    private val purchaseReward: PurchaseRewardUseCase,
    observeSession: ObserveShopSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

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
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            runCatching {
                val userId = currentUserId
                val rewards = getRewards()
                val points = userId?.let { getPointsBalance(it) } ?: 0
                val purchases = userId?.let { getPurchases(it) } ?: emptyList()
                _uiState.update {
                    it.copy(rewards = rewards, points = points, purchases = purchases, isLoading = false)
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun onPurchase(reward: Reward) {
        val userId = currentUserId ?: return
        if (_uiState.value.purchasingRewardId != null) return

        if (_uiState.value.points < reward.pointsCost) {
            _uiState.update { it.copy(hasInsufficientPoints = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(purchasingRewardId = reward.id, hasInsufficientPoints = false) }
            try {
                purchaseReward(userId, reward)
                refresh()
            } catch (_: Exception) {
                _uiState.update { it.copy(hasInsufficientPoints = true) }
            } finally {
                _uiState.update { it.copy(purchasingRewardId = null) }
            }
        }
    }
}
