package com.smartcity.greenpassport.core.model.community

import kotlinx.coroutines.flow.Flow

interface MessageNotificationsRepository {
    fun observeIsEnabled(userId: String): Flow<Boolean>
    suspend fun setEnabled(isEnabled: Boolean, userId: String)
}
