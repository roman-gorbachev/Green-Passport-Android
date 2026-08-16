package com.smartcity.greenpassport.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesSettingsStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : LocalSettingsStore {

    override fun getBoolean(key: String, default: Boolean): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[booleanPreferencesKey(key)] ?: default }

    override suspend fun setBoolean(key: String, value: Boolean) {
        dataStore.edit { preferences -> preferences[booleanPreferencesKey(key)] = value }
    }

    override fun getString(key: String, default: String?): Flow<String?> =
        dataStore.data.map { preferences -> preferences[stringPreferencesKey(key)] ?: default }

    override suspend fun setString(key: String, value: String) {
        dataStore.edit { preferences -> preferences[stringPreferencesKey(key)] = value }
    }
}
