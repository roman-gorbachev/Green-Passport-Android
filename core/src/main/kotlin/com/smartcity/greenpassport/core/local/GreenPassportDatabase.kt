package com.smartcity.greenpassport.core.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameProgressEntity::class], version = 1, exportSchema = false)
abstract class GreenPassportDatabase : RoomDatabase() {
    abstract fun gameProgressDao(): GameProgressDao
}
