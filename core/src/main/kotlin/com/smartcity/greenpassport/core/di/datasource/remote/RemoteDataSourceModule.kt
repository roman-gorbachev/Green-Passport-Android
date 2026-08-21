package com.smartcity.greenpassport.core.di.datasource.remote

import com.smartcity.greenpassport.core.datasource.remote.repository.AchievementsRepositoryImpl
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreCommunityRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreEcoTipsRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreEventsRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreFavoritesRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreFeedbackRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreHistoryRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreMapPointsRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreShopRepository
import com.smartcity.greenpassport.core.datasource.remote.repository.FirestoreTasksRepository
import com.smartcity.greenpassport.core.model.AchievementsRepository
import com.smartcity.greenpassport.core.model.CommunityRepository
import com.smartcity.greenpassport.core.model.EcoTipsRepository
import com.smartcity.greenpassport.core.model.EventsRepository
import com.smartcity.greenpassport.core.model.FavoritesRepository
import com.smartcity.greenpassport.core.model.FeedbackRepository
import com.smartcity.greenpassport.core.model.HistoryRepository
import com.smartcity.greenpassport.core.model.MapPointsRepository
import com.smartcity.greenpassport.core.model.ShopRepository
import com.smartcity.greenpassport.core.model.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RemoteDataSourceModule {
    @Binds
    fun bindAchievementsRepository(impl: AchievementsRepositoryImpl): AchievementsRepository

    @Binds
    fun bindCommunityRepository(impl: FirestoreCommunityRepository): CommunityRepository

    @Binds
    fun bindEcoTipsRepository(impl: FirestoreEcoTipsRepository): EcoTipsRepository

    @Binds
    fun bindEventsRepository(impl: FirestoreEventsRepository): EventsRepository

    @Binds
    fun bindFavoritesRepository(impl: FirestoreFavoritesRepository): FavoritesRepository

    @Binds
    fun bindShopRepository(impl: FirestoreShopRepository): ShopRepository

    @Binds
    fun bindTasksRepository(impl: FirestoreTasksRepository): TasksRepository

    @Binds
    fun bindMapPointsRepository(impl: FirestoreMapPointsRepository): MapPointsRepository

    @Binds
    fun bindFeedbackRepository(impl: FirestoreFeedbackRepository): FeedbackRepository

    @Binds
    fun bindHistoryRepository(impl: FirestoreHistoryRepository): HistoryRepository
}
