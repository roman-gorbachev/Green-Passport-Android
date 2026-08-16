package com.smartcity.greenpassport.core.local

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "greenpassport.db"

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalModule {

    @Binds
    abstract fun bindGameProgressRepository(impl: RoomGameProgressRepository): GameProgressRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): GreenPassportDatabase =
            Room.databaseBuilder(context, GreenPassportDatabase::class.java, DATABASE_NAME).build()

        @Provides
        fun provideGameProgressDao(database: GreenPassportDatabase): GameProgressDao =
            database.gameProgressDao()
    }
}
