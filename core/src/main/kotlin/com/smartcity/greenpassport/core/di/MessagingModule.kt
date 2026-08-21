package com.smartcity.greenpassport.core.di

import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.smartcity.greenpassport.core.messaging.helpers.AndroidRewardNotifier
import com.smartcity.greenpassport.core.messaging.helpers.ReminderScheduler
import com.smartcity.greenpassport.core.messaging.helpers.RewardNotifier
import com.smartcity.greenpassport.core.messaging.repository.NotificationsRepository
import com.smartcity.greenpassport.core.messaging.worker.WorkManagerReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingModule {

    @Binds
    abstract fun bindNotificationsRepository(impl: NotificationsRepository): NotificationsRepository

    @Binds
    abstract fun bindReminderScheduler(impl: WorkManagerReminderScheduler): ReminderScheduler

    @Binds
    abstract fun bindRewardNotifier(impl: AndroidRewardNotifier): RewardNotifier

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseMessaging(): FirebaseMessaging = Firebase.messaging
    }
}
