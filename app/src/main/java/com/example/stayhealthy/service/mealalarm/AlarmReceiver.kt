package com.example.stayhealthy.service.mealalarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.stayhealthy.R
import com.example.stayhealthy.config.CHANNEL_ID_NOTIFICATIONS
import com.example.stayhealthy.repositories.ResourceRepository
import com.example.stayhealthy.ui.activities.HomeActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val res: ResourceRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {

        val calendar = GregorianCalendar()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        var notificationBody = ""
        var notificationId = 0

        when (hourOfDay) {
            in 7..10 -> {
                notificationBody = res.getString(R.string.it_is_breakfast_time)
                notificationId = MealTimeNotificationHelper.ALARM_TYPE_RTC_BREAKFAST
            }
            in 11..13 -> {
                notificationBody = res.getString(R.string.it_is_lunch_time)
                notificationId = MealTimeNotificationHelper.ALARM_TYPE_RTC_LUNCH
            }
            in 17..21 -> {
                notificationBody = res.getString(R.string.it_is_dinner_time)
                notificationId = MealTimeNotificationHelper.ALARM_TYPE_RTC_DINNER
            }
        }

        context?.let {
            showAlarmNotification(
                it, res.getString(R.string.time_to_eat), notificationBody,
                createMealAlarmNotificationPendingIntent(it, notificationId), notificationId
            )
        }
    }


    private fun showAlarmNotification(
        context: Context,
        title: String?,
        body: String,
        pendingIntent: PendingIntent,
        notificationId: Int
    ) {

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_NOTIFICATIONS)
            .setSmallIcon(R.drawable.image_app)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        val notification = notificationBuilder.build()

        MealTimeNotificationHelper.getNotificationManager(context)
            .notify(notificationId, notification)
    }


    private fun createMealAlarmNotificationPendingIntent(context: Context, id: Int): PendingIntent {
        val notificationIntent = Intent(context, HomeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        return PendingIntent.getActivity(
            context,
            id,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

}