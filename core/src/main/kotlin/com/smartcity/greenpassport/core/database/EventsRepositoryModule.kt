package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.EventsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EventsRepositoryModule {

    @Binds
    abstract fun bindEventsRepository(impl: FirestoreEventsRepository): EventsRepository
}
