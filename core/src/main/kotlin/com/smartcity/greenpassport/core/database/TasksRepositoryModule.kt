package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TasksRepositoryModule {

    @Binds
    abstract fun bindTasksRepository(impl: FirestoreTasksRepository): TasksRepository
}
