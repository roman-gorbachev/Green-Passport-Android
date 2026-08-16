package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.EcoTipsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EcoTipsRepositoryModule {

    @Binds
    abstract fun bindEcoTipsRepository(impl: FirestoreEcoTipsRepository): EcoTipsRepository
}
