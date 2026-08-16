package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.AchievementsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AchievementsRepositoryModule {

    @Binds
    abstract fun bindAchievementsRepository(impl: AchievementsRepositoryImpl): AchievementsRepository
}
