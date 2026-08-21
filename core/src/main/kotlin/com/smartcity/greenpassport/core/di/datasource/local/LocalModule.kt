package com.smartcity.greenpassport.core.di.datasource.local

import android.content.Context
import androidx.room.Room
import com.smartcity.greenpassport.core.datasource.local.GreenPassportDatabase
import com.smartcity.greenpassport.core.datasource.local.dao.GameProgressDao
import com.smartcity.greenpassport.core.datasource.local.dao.NotificationLogDao
import com.smartcity.greenpassport.core.datasource.local.repository.GameProgressRepository
import com.smartcity.greenpassport.core.datasource.local.repository.NotificationLogRepository
import com.smartcity.greenpassport.core.datasource.local.repository.RoomGameProgressRepository
import com.smartcity.greenpassport.core.datasource.local.repository.RoomNotificationLogRepository
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

    @Binds
    abstract fun bindNotificationLogRepository(impl: RoomNotificationLogRepository): NotificationLogRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): GreenPassportDatabase =
            Room.databaseBuilder(context, GreenPassportDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()

        @Provides
        fun provideGameProgressDao(database: GreenPassportDatabase): GameProgressDao =
            database.gameProgressDao()

        @Provides
        fun provideNotificationLogDao(database: GreenPassportDatabase): NotificationLogDao =
            database.notificationLogDao()
    }
}
