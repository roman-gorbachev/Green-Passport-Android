package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryRepositoryModule {

    @Binds
    abstract fun bindHistoryRepository(impl: FirestoreHistoryRepository): HistoryRepository
}
