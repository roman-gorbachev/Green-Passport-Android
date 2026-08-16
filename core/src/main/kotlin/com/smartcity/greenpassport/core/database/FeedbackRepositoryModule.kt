package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.FeedbackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FeedbackRepositoryModule {

    @Binds
    abstract fun bindFeedbackRepository(impl: FirestoreFeedbackRepository): FeedbackRepository
}
