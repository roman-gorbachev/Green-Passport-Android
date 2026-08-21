package com.smartcity.greenpassport.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smartcity.greenpassport.core.datasource.local.entities.NotificationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationLogDao {
    @Insert
    suspend fun insert(entry: NotificationLogEntity)

    @Query("SELECT * FROM notification_log ORDER BY sentAtEpochMillis DESC")
    fun observeAll(): Flow<List<NotificationLogEntity>>
}
