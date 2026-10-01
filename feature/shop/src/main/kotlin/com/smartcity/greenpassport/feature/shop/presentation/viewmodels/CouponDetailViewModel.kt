package com.smartcity.greenpassport.feature.shop.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.rewards.RewardFailure
import com.smartcity.greenpassport.core.model.rewards.RewardFailureException
import com.smartcity.greenpassport.feature.shop.domain.MarkCouponUsedUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveCouponUseCase
import com.smartcity.greenpassport.feature.shop.domain.ObserveRewardsUseCase
import com.smartcity.greenpassport.feature.shop.presentation.state.CouponDetailUiState
import com.smartcity.greenpassport.feature.shop.presentation.state.CouponItem
import com.smartcity.greenpassport.feature.shop.presentation.state.couponQrPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CouponDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeCoupon: ObserveCouponUseCase,
    private val observeRewards: ObserveRewardsUseCase,
    private val markCouponUsed: MarkCouponUsedUseCase,
) : ViewModel() {

    private val couponId: String = checkNotNull(savedStateHandle["couponId"])

    private val actions = MutableStateFlow(MarkState())

    val uiState = observeCouponDetailUiState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        CouponDetailUiState(),
    )

    fun onMarkUsed() {
        if (actions.value.isMarking) return
        viewModelScope.launch {
            actions.update { MarkState(isMarking = true) }
            runCatching { markCouponUsed(couponId) }
                .onSuccess { actions.update { MarkState() } }
                .onFailure { error ->
                    Log.w(TAG, "Failed to mark coupon used", error)
                    val failure = (error as? RewardFailureException)?.failure ?: RewardFailure.UNKNOWN
                    actions.update { MarkState(failure = failure) }
                }
        }
    }

    private fun observeCouponDetailUiState(): Flow<CouponDetailUiState> {
        val item = combine(observeCoupon(couponId), observeRewards().catch { emit(emptyList()) }) { coupon, rewards ->
            coupon?.let { CouponItem(it, rewards.firstOrNull { reward -> reward.id == it.rewardId }) }
        }
        return combine(item, actions) { currentItem, markState ->
            CouponDetailUiState(
                item = currentItem,
                qrPayload = currentItem?.coupon?.let(::couponQrPayload),
                nowEpochMillis = System.currentTimeMillis(),
                isLoading = currentItem == null,
                isMarking = markState.isMarking,
                failure = markState.failure,
            )
        }
            .onStart { emit(CouponDetailUiState()) }
            .catch { emit(CouponDetailUiState(isLoading = false, failure = RewardFailure.UNKNOWN)) }
    }

    private data class MarkState(
        val isMarking: Boolean = false,
        val failure: RewardFailure? = null,
    )

    companion object {
        private const val TAG = "CouponDetailViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
