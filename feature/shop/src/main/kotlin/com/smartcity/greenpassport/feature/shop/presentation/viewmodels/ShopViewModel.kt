package com.smartcity.greenpassport.feature.shop.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.core.model.Coupon
import com.smartcity.greenpassport.core.model.Reward
import com.smartcity.greenpassport.feature.shop.domain.ObservePurchasesUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveRewardsUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveShopPointsBalanceUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveShopSessionUseCase
import com.smartcity.greenpassport.feature.shop.domain.PurchaseRewardUseCase
import com.smartcity.greenpassport.feature.shop.presentation.state.ShopUiState
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ShopViewModel @Inject constructor(
    private val observeRewards: ObserveRewardsUseCase,
    private val observePurchases: ObservePurchasesUseCase,
    private val observePointsBalance: ObserveShopPointsBalanceUseCase,
    private val purchaseReward: PurchaseRewardUseCase,
    observeSession: ObserveShopSessionUseCase,
) : ViewModel() {

    private val actions = MutableStateFlow(ShopActions())

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private var currentUserId: String? = null

    val uiState = observeShopUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        ShopUiState(),
    )

    fun refresh() {
        retryRequests.tryEmit(Unit)
    }

    fun onPurchase(reward: Reward) {
        if (currentUserId == null || actions.value.purchasingRewardId != null) return
        if (uiState.value.points < reward.pointsCost) {
            actions.update { it.copy(hasInsufficientPoints = true) }
            return
        }
        viewModelScope.launch {
            actions.update { it.copy(purchasingRewardId = reward.id, hasInsufficientPoints = false) }
            runCatching { purchaseReward(reward) }
                .onFailure { error ->
                    Log.w(TAG, "Failed to purchase reward", error)
                    actions.update { it.copy(hasInsufficientPoints = true) }
                }
            actions.update { it.copy(purchasingRewardId = null) }
        }
    }

    private fun observeShopUiState(sessions: Flow<AuthSession?>): Flow<ShopUiState> {
        val data = combine(sessions, retryRequests.onStart { emit(Unit) }) { session, _ -> session }
            .flatMapLatest { session ->
                currentUserId = session?.userId
                observeShopData(session?.userId)
            }
        return combine(data, actions) { shopData, currentActions ->
            shopData.copy(
                purchasingRewardId = currentActions.purchasingRewardId,
                hasInsufficientPoints = currentActions.hasInsufficientPoints,
            )
        }
    }

    private fun observeShopData(userId: String?): Flow<ShopUiState> {
        val points = userId?.let { observePointsBalance(it).catch { emit(0) } } ?: flowOf(0)
        val purchases = userId?.let { observePurchases(it).catch { emit(emptyList<Coupon>()) } } ?: flowOf(emptyList())
        return combine(observeRewards(), points, purchases) { rewards, currentPoints, currentPurchases ->
            ShopUiState(
                points = currentPoints,
                rewards = rewards,
                purchases = currentPurchases.sortedByDescending { it.redeemedAtEpochMillis },
                isLoading = false,
            )
        }
            .onStart { emit(ShopUiState()) }
            .catch { emit(ShopUiState(isLoading = false, hasError = true)) }
    }

    private data class ShopActions(
        val purchasingRewardId: String? = null,
        val hasInsufficientPoints: Boolean = false,
    )

    companion object {
        private const val TAG = "ShopViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
