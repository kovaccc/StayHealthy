package com.example.stayhealthy

import android.app.Application
import com.example.stayhealthy.di.*
import com.google.firebase.database.FirebaseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App :
        Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance()
                .setPersistenceEnabled(true)

        startKoin {
            androidContext(this@App)
            modules(
                    arrayListOf(
                            viewModelModule,
                            preferencesModule,
                            repositoryModule,
                            commonModule,
                            databaseModule,
                            storageModule
                    )
            )
        }
    }
}

