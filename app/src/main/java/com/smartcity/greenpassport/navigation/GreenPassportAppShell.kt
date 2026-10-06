package com.smartcity.greenpassport.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.smartcity.greenpassport.core.model.community.ChatId
import com.smartcity.greenpassport.core.navigation.destination

@Composable
fun GreenPassportAppShell(
    pendingChatId: String?,
    onChatOpened: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    LaunchedEffect(pendingChatId) {
        val chatId = pendingChatId ?: return@LaunchedEffect
        navController.navigate(ChatId.fromRawValue(chatId).destination)
        onChatOpened()
    }
    AppNavHost(navController = navController, modifier = modifier)
}
