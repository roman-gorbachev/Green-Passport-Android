package com.smartcity.greenpassport.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.smartcity.greenpassport.core.datastore.AppCompatLanguageRepository
import com.smartcity.greenpassport.core.datastore.LocalAppSettingsRepository
import com.smartcity.greenpassport.core.datastore.LocalSettingsStore
import com.smartcity.greenpassport.core.datastore.PackageManagerAppIconRepository
import com.smartcity.greenpassport.core.datastore.PreferencesSettingsStore
import com.smartcity.greenpassport.core.model.settings.AppIconRepository
import com.smartcity.greenpassport.core.model.settings.AppLanguageRepository
import com.smartcity.greenpassport.core.model.settings.AppSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PREFERENCES_FILE_NAME = "greenpassport_settings"

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreModule {

    @Binds
    abstract fun bindLocalSettingsStore(impl: PreferencesSettingsStore): LocalSettingsStore

    @Binds
    abstract fun bindAppSettingsRepository(impl: LocalAppSettingsRepository): AppSettingsRepository

    @Binds
    abstract fun bindAppLanguageRepository(impl: AppCompatLanguageRepository): AppLanguageRepository

    @Binds
    abstract fun bindAppIconRepository(impl: PackageManagerAppIconRepository): AppIconRepository

    companion object {
        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile(PREFERENCES_FILE_NAME) },
            )
    }
}
