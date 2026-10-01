package com.smartcity.greenpassport.feature.shop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.auth.AuthSession
import com.smartcity.greenpassport.feature.shop.domain.ObservePurchasesUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveRewardsUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveShopSessionUseCase
import com.smartcity.greenpassport.feature.shop.presentation.state.CouponItem
import com.smartcity.greenpassport.feature.shop.presentation.state.CouponsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CouponsViewModel @Inject constructor(
    private val observeRewards: ObserveRewardsUseCase,
    private val observePurchases: ObservePurchasesUseCase,
    observeSession: ObserveShopSessionUseCase,
) : ViewModel() {

    private val retryRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val uiState = observeCouponsUiState(observeSession()).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        CouponsUiState(),
    )

    fun retry() {
        retryRequests.tryEmit(Unit)
    }

    private fun observeCouponsUiState(sessions: Flow<AuthSession?>): Flow<CouponsUiState> {
        return combine(sessions, retryRequests.onStart { emit(Unit) }) { session, _ -> session }
            .flatMapLatest { session ->
                val purchases = session?.let { observePurchases(it.userId) } ?: flowOf(emptyList())
                combine(observeRewards(), purchases) { rewards, coupons ->
                    CouponsUiState(
                        items = coupons
                            .sortedByDescending { it.redeemedAtEpochMillis }
                            .map { coupon -> CouponItem(coupon, rewards.firstOrNull { it.id == coupon.rewardId }) },
                        nowEpochMillis = System.currentTimeMillis(),
                        isLoading = false,
                    )
                }
                    .onStart { emit(CouponsUiState()) }
                    .catch { emit(CouponsUiState(isLoading = false, hasError = true)) }
            }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
