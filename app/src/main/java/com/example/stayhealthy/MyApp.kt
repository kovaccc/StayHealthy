package com.example.stayhealthy

import android.app.Application
import com.example.stayhealthy.module.preferencesModule
import com.example.stayhealthy.module.repositoryModule
import com.example.stayhealthy.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApp : Application() { // instance of application class, also you need to add on manifest android:name:".MyApp"
    override fun onCreate()
    {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp) // access to your application
            modules(listOf(viewModelModule, preferencesModule, repositoryModule)) //you can add list of modules here
        }
    }
}

