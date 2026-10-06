package com.smartcity.greenpassport.core.messaging.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartcity.greenpassport.core.auth.AuthRepository
import com.smartcity.greenpassport.core.messaging.helpers.ChatNotifier
import com.smartcity.greenpassport.core.messaging.helpers.OpenChatTracker
import com.smartcity.greenpassport.core.messaging.repository.INotificationsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationsRepository: INotificationsRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var chatNotifier: ChatNotifier

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        scope.launch {
            val userId = authRepository.session.first()?.userId ?: return@launch
            runCatching { notificationsRepository.registerToken(userId) }
                .onFailure { error -> Log.w(TAG, "Failed to register the new token", error) }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val chatId = data[KEY_CHAT_ID]
        if (data[KEY_TYPE] != TYPE_CHAT || chatId == null || OpenChatTracker.openChatId == chatId) return
        chatNotifier.show(chatId = chatId, chatTitle = data[KEY_CHAT_TITLE], body = data[KEY_BODY].orEmpty())
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ChatMessagingService"
        private const val KEY_TYPE = "type"
        private const val KEY_CHAT_ID = "chatId"
        private const val KEY_CHAT_TITLE = "chatTitle"
        private const val KEY_BODY = "body"
        private const val TYPE_CHAT = "chat"
    }
}
