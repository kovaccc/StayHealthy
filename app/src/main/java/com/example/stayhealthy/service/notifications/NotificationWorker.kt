package com.example.stayhealthy.service.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stayhealthy.R
import com.example.stayhealthy.config.BUNDLE_KEY_NOTIFICATION
import com.example.stayhealthy.config.CHANNEL_ID_NOTIFICATIONS
import com.example.stayhealthy.data.models.requests.NotificationRequest
import com.example.stayhealthy.repositories.UserRepository
import com.example.stayhealthy.repositories.implementation.UserRepositoryImpl
import com.example.stayhealthy.service.notifications.MealTimeNotificationHelper.ALARM_TYPE_RTC_BREAKFAST
import com.example.stayhealthy.service.notifications.MealTimeNotificationHelper.ALARM_TYPE_RTC_DINNER
import com.example.stayhealthy.service.notifications.MealTimeNotificationHelper.ALARM_TYPE_RTC_LUNCH
import com.example.stayhealthy.ui.activities.HomeActivity
import com.example.stayhealthy.util.failableCall
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class NotificationWorker(private val context: Context, parameters: WorkerParameters) :
        CoroutineWorker(context, parameters), KoinComponent {

    private val userRepository: UserRepository by inject<UserRepositoryImpl>()

    override suspend fun doWork(): Result {

        val notificationString = inputData.getString(BUNDLE_KEY_NOTIFICATION)
        val notification = Gson().fromJson(notificationString, NotificationRequest::class.java)
        Log.d("IVAN", "I A M IN SERVICE ${userRepository.checkUserLoggedIn()}")
        failableCall {
            Log.d("IVAN", "I A M IN SERVICE ${userRepository.checkUserLoggedIn()}")
            if (withContext(IO) { userRepository.checkUserLoggedIn() != null }) {
                Log.d("IVAN", "user is logged in ")
                var pendingIntent: PendingIntent? = null
                when (notification.id) {
                    ALARM_TYPE_RTC_BREAKFAST, ALARM_TYPE_RTC_LUNCH, ALARM_TYPE_RTC_DINNER -> {
                        pendingIntent = createMealAlarmNotificationPendingIntent(context, notification.id)
                    }
                    else -> {

                    }
                }

                showNotification(
                        context, notification.title, notification.body, pendingIntent,
                        notification.id
                )

            }

        }

        return Result.success()
    }

}

fun showNotification(
        context: Context,
        title: String?,
        body: String,
        pendingIntent: PendingIntent?,
        notificationId: Int
) {

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_NOTIFICATIONS)
            .setSmallIcon(R.drawable.image_app)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


    val notification = notificationBuilder.build()

    NotificationManagerCompat.from(context).notify(notificationId, notification)
}


fun createMealAlarmNotificationPendingIntent(context: Context, id: Int): PendingIntent {
    val notificationIntent = Intent(context, HomeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    return PendingIntent.getActivity(
            context,
            id,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )
}
