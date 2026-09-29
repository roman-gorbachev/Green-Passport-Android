package com.smartcity.greenpassport.core.model.profile

import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(userId: String): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
}
