package com.example.stayhealthy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stayhealthy.data.database.dao.UserDao
import com.example.stayhealthy.data.models.persistance.DBUser

@Database(
        entities = [DBUser::class],
        version = 1,
        exportSchema = false
)
abstract class StayHealthyDB : RoomDatabase() {
    abstract fun userDao(): UserDao
}