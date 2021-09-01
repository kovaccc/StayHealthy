package com.example.stayhealthy.service.mealalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            context?.let {
                MealTimeNotificationHelper.scheduleRepeatingRTCMealTimeNotification(it)
            }
        }
    }
}