package com.smartcity.greenpassport.feature.community.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.model.community.ChatSettings
import com.smartcity.greenpassport.feature.community.domain.ObserveChatSettingsUseCase
import com.smartcity.greenpassport.feature.community.domain.ObserveCommunitySessionUseCase
import com.smartcity.greenpassport.feature.community.domain.UpdateChatSettingsUseCase
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatMuteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeSession: ObserveCommunitySessionUseCase,
    private val observeChatSettings: ObserveChatSettingsUseCase,
    private val updateChatSettings: UpdateChatSettingsUseCase,
) : ViewModel() {

    private val chatId: ChatId = savedStateHandle.get<String>(GROUP_ID_KEY)?.let(ChatId::Group) ?: ChatId.Forum

    private val userId = observeSession()
        .map { it?.userId }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val settings = userId.flatMapLatest { id ->
        if (id == null) {
            flowOf(ChatSettings.defaults(chatId))
        } else {
            observeChatSettings(id)
                .map { all -> all[chatId] ?: ChatSettings.defaults(chatId) }
                .catch { emit(ChatSettings.defaults(chatId)) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ChatSettings.defaults(chatId))

    private val mutedOverride = MutableStateFlow<Boolean?>(null)

    val isMuted = combine(settings, mutedOverride) { current, override -> override ?: current.isMuted }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        ChatSettings.defaults(chatId).isMuted,
    )

    val canMute = userId.map { it != null }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        false,
    )

    fun onToggle() {
        val currentUserId = userId.value ?: return
        val updated = settings.value.copy(isMuted = !isMuted.value)
        mutedOverride.value = updated.isMuted
        viewModelScope.launch {
            runCatching { updateChatSettings(updated, currentUserId) }
                .onFailure { error -> Log.w(TAG, "Failed to update chat settings", error) }
            mutedOverride.value = null
        }
    }

    companion object {
        private const val TAG = "ChatMuteViewModel"
        private const val GROUP_ID_KEY = "groupId"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
