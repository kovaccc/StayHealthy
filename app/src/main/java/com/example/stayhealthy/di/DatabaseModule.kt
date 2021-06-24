package com.example.stayhealthy.di

import androidx.room.Room
import com.example.stayhealthy.config.DATABASE_NAME
import com.example.stayhealthy.data.database.StayHealthyDB
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidApplication(), StayHealthyDB::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .addMigrations()
                .build()
    }

    single { get<StayHealthyDB>().userDao() }
}