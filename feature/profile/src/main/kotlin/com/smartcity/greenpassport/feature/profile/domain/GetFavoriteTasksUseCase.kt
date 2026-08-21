package com.smartcity.greenpassport.feature.profile.domain

import com.smartcity.greenpassport.core.model.FavoritesRepository
import com.smartcity.greenpassport.core.model.Task
import com.smartcity.greenpassport.core.model.TasksRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetFavoriteTasksUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val tasksRepository: TasksRepository,
) {
    suspend operator fun invoke(userId: String): List<Task> {
        val favoriteIds = favoritesRepository.observeFavoriteTaskIds(userId).first()
        return tasksRepository.getTasks().filter { favoriteIds.contains(it.id) }
    }
}
