package com.example.stayhealthy.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.stayhealthy.R
import com.example.stayhealthy.config.BUNDLE_KEY_ALARM_TYPE
import com.example.stayhealthy.config.BUNDLE_KEY_NOTIFICATION
import com.example.stayhealthy.data.models.requests.NotificationRequest
import com.example.stayhealthy.repositories.ResourceRepository
import com.example.stayhealthy.service.notifications.MealTimeNotificationHelper.ALARM_TYPE_RTC_BREAKFAST
import com.example.stayhealthy.service.notifications.MealTimeNotificationHelper.ALARM_TYPE_RTC_DINNER
import com.example.stayhealthy.service.notifications.MealTimeNotificationHelper.ALARM_TYPE_RTC_LUNCH
import com.example.stayhealthy.service.notifications.NotificationWorker
import com.google.gson.Gson
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*


private const val TAG = "AlarmReceiver"

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val res: ResourceRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {

        val alarmRequestCode = intent?.getIntExtra(BUNDLE_KEY_ALARM_TYPE, 0)
        when (alarmRequestCode) {
            ALARM_TYPE_RTC_BREAKFAST, ALARM_TYPE_RTC_LUNCH, ALARM_TYPE_RTC_DINNER -> {
                handleMealNotificationAlarm(context)
            }
            else -> {
            }

        }
    }

    private fun handleMealNotificationAlarm(context: Context?) {

        val calendar = GregorianCalendar()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        var notificationBody = ""
        var notificationId = 0

        when (hourOfDay) {
            in 7..10 -> {
                notificationBody = res.getString(R.string.it_is_breakfast_time)
                notificationId = ALARM_TYPE_RTC_BREAKFAST
            }
            in 11..15 -> {
                notificationBody = res.getString(R.string.it_is_lunch_time)
                notificationId = ALARM_TYPE_RTC_LUNCH
            }
            in 17..21 -> {
                notificationBody = res.getString(R.string.it_is_dinner_time)
                notificationId = ALARM_TYPE_RTC_DINNER
            }
        }

        context?.let {
            val notificationRequest = NotificationRequest(notificationId, res.getString(R.string.time_to_eat), notificationBody)

            val uploadWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(
                            workDataOf(
                                    BUNDLE_KEY_NOTIFICATION
                                            to Gson().toJson(notificationRequest)
                            )
                    )
                    .build()
            WorkManager.getInstance(context).enqueueUniqueWork("NotificationWorker", ExistingWorkPolicy.KEEP, uploadWork)
        }

    }

}