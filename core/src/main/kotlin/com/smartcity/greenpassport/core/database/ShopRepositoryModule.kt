package com.smartcity.greenpassport.core.database

import com.smartcity.greenpassport.core.model.ShopRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ShopRepositoryModule {

    @Binds
    abstract fun bindShopRepository(impl: FirestoreShopRepository): ShopRepository
}
