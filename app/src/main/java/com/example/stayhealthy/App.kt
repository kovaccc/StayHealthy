package com.example.stayhealthy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.stayhealthy.config.CHANNEL_ID_NOTIFICATIONS
import com.example.stayhealthy.config.CHANNEL_NAME
import com.example.stayhealthy.di.*
import com.example.stayhealthy.service.notifications.MealTimeNotificationHelper
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


        createNotificationChannel()

        scheduleMealAlarms()


    }

    private fun scheduleMealAlarms() {
        applicationContext?.let { context ->
            MealTimeNotificationHelper.scheduleRepeatingRTCMealTimeNotification(applicationContext)
            MealTimeNotificationHelper.enableBootReceiver(applicationContext)
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_NOTIFICATIONS, CHANNEL_NAME, importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

