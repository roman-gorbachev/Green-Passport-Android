package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.FavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesRepositoryModule {

    @Binds
    abstract fun bindFavoritesRepository(impl: FirestoreFavoritesRepository): FavoritesRepository
}
