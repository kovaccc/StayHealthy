package com.example.stayhealthy.di

import com.example.stayhealthy.data.database.storage.ProfileStorage
import org.koin.dsl.module

val storageModule = module {
    single { ProfileStorage(get()) }
}