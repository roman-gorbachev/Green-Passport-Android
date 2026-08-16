package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.CommunityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityRepositoryModule {

    @Binds
    abstract fun bindCommunityRepository(impl: FirestoreCommunityRepository): CommunityRepository
}
