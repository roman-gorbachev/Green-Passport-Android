package com.smartcity.greenpassport.core.datastore

import kotlinx.coroutines.flow.Flow

interface LocalSettingsStore {
    fun getBoolean(key: String, default: Boolean): Flow<Boolean>
    suspend fun setBoolean(key: String, value: Boolean)

    fun getString(key: String, default: String?): Flow<String?>
    suspend fun setString(key: String, value: String)
}
