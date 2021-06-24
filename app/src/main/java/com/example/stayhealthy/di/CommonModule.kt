package com.example.stayhealthy.di


import com.example.stayhealthy.data.PrefsHelper
import com.example.stayhealthy.utils.FirebaseStorageManager
import org.koin.core.qualifier.named
import org.koin.dsl.module

val commonModule = module {
    single { FirebaseStorageManager() }
    single { PrefsHelper(get(named("datePrefs")), get(named("appPrefs"))) }
}





