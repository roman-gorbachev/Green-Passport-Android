package com.smartcity.greenpassport.core.di

import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.smartcity.greenpassport.core.storage.FirebaseMediaRepository
import com.smartcity.greenpassport.core.storage.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    abstract fun bindMediaRepository(impl: FirebaseMediaRepository): MediaRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
    }
}
