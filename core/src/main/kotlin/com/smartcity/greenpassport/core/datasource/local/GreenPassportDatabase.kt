package com.smartcity.greenpassport.core.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartcity.greenpassport.core.datasource.local.dao.GameProgressDao
import com.smartcity.greenpassport.core.datasource.local.dao.NotificationLogDao
import com.smartcity.greenpassport.core.datasource.local.entities.GameProgressEntity
import com.smartcity.greenpassport.core.datasource.local.entities.NotificationLogEntity

@Database(
    entities = [GameProgressEntity::class, NotificationLogEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class GreenPassportDatabase : RoomDatabase() {
    abstract fun gameProgressDao(): GameProgressDao
    abstract fun notificationLogDao(): NotificationLogDao
}
