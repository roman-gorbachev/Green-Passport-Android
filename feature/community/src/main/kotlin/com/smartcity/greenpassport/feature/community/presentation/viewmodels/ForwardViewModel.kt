package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.moderation.ContentRejectedException
import com.smartcity.greenpassport.feature.community.domain.ForwardMessageUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveMyGroupsUseCase
import com.smartcity.greenpassport.feature.community.presentation.state.ForwardUiState
import com.smartcity.greenpassport.feature.community.presentation.state.MessageTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ForwardViewModel @Inject constructor(
    observeSession: ObserveCommunitySessionUseCase,
    private val observeMyGroups: ObserveMyGroupsUseCase,
    private val forwardMessage: ForwardMessageUseCase,
) : ViewModel() {

    private val local = MutableStateFlow(ForwardUiState())

    private val userId = observeSession()
        .map { it?.userId }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val uiState = combine(local, observeGroups()) { state, groups -> state.copy(groups = groups) }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        ForwardUiState(),
    )

    fun onForward(message: MessageTarget, chat: ChatId) {
        val senderId = userId.value ?: return
        if (local.value.sendingChatId != null || local.value.isSent) return
        viewModelScope.launch {
            local.update { it.copy(sendingChatId = chat, isSendFailed = false, isTextRejected = false) }
            runCatching { forwardMessage(message.text, message.forwardOrigin, chat, senderId) }
                .onSuccess { local.update { it.copy(sendingChatId = null, isSent = true) } }
                .onFailure { error ->
                    Log.w(TAG, "Failed to forward message", error)
                    val isRejected = error is ContentRejectedException
                    local.update {
                        it.copy(
                            sendingChatId = null,
                            isTextRejected = isRejected,
                            isSendFailed = !isRejected
                        )
                    }
                }
        }
    }

    private fun observeGroups() = userId.flatMapLatest { id ->
        if (id == null) {
            flowOf(emptyList())
        } else {
            observeMyGroups(id).map { groups -> groups.sortedBy { it.name.lowercase() } }.catch { emit(emptyList()) }
        }
    }

    companion object {
        private const val TAG = "ForwardViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
