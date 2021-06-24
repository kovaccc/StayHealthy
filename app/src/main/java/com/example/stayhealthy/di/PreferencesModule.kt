package com.example.stayhealthy.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.stayhealthy.config.DATE_PREFERENCES_FILE_KEY
import com.example.stayhealthy.config.STAY_HEALTHY_PREFS_FILE_KEY
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val preferencesModule = module {
    single(named("datePrefs")) { provideDatePreferences(androidApplication()) }
    single(named("appPrefs")) { provideAppPreferences(androidApplication()) }
}

private fun provideDatePreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(DATE_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

private fun provideAppPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(STAY_HEALTHY_PREFS_FILE_KEY, Context.MODE_PRIVATE)

