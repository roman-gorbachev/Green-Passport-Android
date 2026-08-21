package com.smartcity.greenpassport.core.di.datasource.remote

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestorePointsRepository
import com.smartcity.greenpassport.core.model.PointsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirestoreModule {

    @Binds
    abstract fun bindPointsRepository(impl: FirestorePointsRepository): PointsRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = Firebase.firestore
    }
}
