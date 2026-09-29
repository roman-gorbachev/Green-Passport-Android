package com.smartcity.greenpassport.core.di

import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.smartcity.greenpassport.core.datasource.remote.functions.CloudFunctionNames
import com.smartcity.greenpassport.core.datasource.remote.functions.FirebaseRewardsRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirebaseModerationRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirebaseTaskSubmissionsRepository
import com.smartcity.greenpassport.core.model.moderation.ModerationRepository
import com.smartcity.greenpassport.core.model.rewards.RewardsRepository
import com.smartcity.greenpassport.core.model.verification.TaskSubmissionsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FunctionsModule {

    @Binds
    abstract fun bindRewardsRepository(impl: FirebaseRewardsRepository): RewardsRepository

    @Binds
    abstract fun bindTaskSubmissionsRepository(impl: FirebaseTaskSubmissionsRepository): TaskSubmissionsRepository

    @Binds
    abstract fun bindModerationRepository(impl: FirebaseModerationRepository): ModerationRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseFunctions(): FirebaseFunctions = Firebase.functions(CloudFunctionNames.REGION)
    }
}
