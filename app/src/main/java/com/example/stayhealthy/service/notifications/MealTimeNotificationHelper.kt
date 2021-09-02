package com.example.stayhealthy.service.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.os.bundleOf
import com.example.stayhealthy.config.BREAKFAST_TIME_HOUR
import com.example.stayhealthy.config.BUNDLE_KEY_ALARM_TYPE
import com.example.stayhealthy.config.DINNER_TIME_HOUR
import com.example.stayhealthy.config.LUNCH_TIME_HOUR
import com.example.stayhealthy.service.alarm.AlarmBootReceiver
import com.example.stayhealthy.service.alarm.AlarmReceiver
import com.example.stayhealthy.util.TimeHelper


object MealTimeNotificationHelper {
    const val ALARM_TYPE_RTC_BREAKFAST = 100
    const val ALARM_TYPE_RTC_LUNCH = 101
    const val ALARM_TYPE_RTC_DINNER = 102
    private var alarmManagersRTC = mutableMapOf<Int, AlarmManager?>()
    private var requestCodeArray =
            arrayListOf(ALARM_TYPE_RTC_BREAKFAST, ALARM_TYPE_RTC_LUNCH, ALARM_TYPE_RTC_DINNER)
    private var alarmIntentsRTC = mutableMapOf<Int, PendingIntent>()

    fun scheduleRepeatingRTCMealTimeNotification(context: Context) {
        cancelMealTimeAlarmRTC()
        val intent = Intent(context, AlarmReceiver::class.java)
        for (requestCode in requestCodeArray) {
            val alarmTime = when (requestCode) {
                ALARM_TYPE_RTC_BREAKFAST -> TimeHelper.getTimeOfCurrentDay(
                        BREAKFAST_TIME_HOUR,
                        0,
                        0,
                        0
                )
                ALARM_TYPE_RTC_LUNCH -> TimeHelper.getTimeOfCurrentDay(LUNCH_TIME_HOUR, 0, 0, 0)
                ALARM_TYPE_RTC_DINNER -> TimeHelper.getTimeOfCurrentDay(DINNER_TIME_HOUR, 0, 0, 0)
                else -> TimeHelper.getTimeOfCurrentDay(BREAKFAST_TIME_HOUR, 0, 0, 0)
            }

            intent.putExtras(bundleOf(BUNDLE_KEY_ALARM_TYPE to requestCode))

            val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmIntentsRTC[requestCode] = pendingIntent
            alarmManagersRTC[requestCode] = context.getSystemService(ALARM_SERVICE) as AlarmManager?
            alarmManagersRTC[requestCode]?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime, AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }

    }


    private fun cancelMealTimeAlarmRTC() {
        for (requestCode in requestCodeArray) {
            alarmManagersRTC[requestCode]?.cancel(alarmIntentsRTC[requestCode])
        }
        alarmIntentsRTC.clear()
    }

    fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    fun enableBootReceiver(context: Context) {
        val receiver = ComponentName(context, AlarmBootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        )
    }

    /**
     * Disable boot receiver when user cancels/opt-out from notifications
     */
    fun disableBootReceiver(context: Context) {
        val receiver = ComponentName(context, AlarmBootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        )
    }
}