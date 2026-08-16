package com.smartcity.greenpassport.core.model

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavoriteTaskIds(userId: String): Flow<Set<String>>
    suspend fun setTaskFavorite(userId: String, taskId: String, isFavorite: Boolean)

    fun observeBookmarkedTipIds(userId: String): Flow<Set<String>>
    suspend fun setTipBookmarked(userId: String, tipId: String, isBookmarked: Boolean)
}
