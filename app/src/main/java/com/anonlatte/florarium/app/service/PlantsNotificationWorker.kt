package com.anonlatte.florarium.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.anonlatte.florarium.BuildConfig
import com.anonlatte.florarium.R
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.extensions.WORKER_ALARM_DATA_KEY
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber

class PlantsNotificationWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        val formattedAlarm = workerParams.inputData.getString(
            WORKER_ALARM_DATA_KEY
        )
        if (formattedAlarm == null) {
            Timber.w("Alarm data wasn't receive.")
            return Result.failure()
        }

        Json.decodeFromString<PlantAlarm>(formattedAlarm).run {
            makeNotification(context, eventTag, plantName)
        }
        return Result.success()
    }

    private fun makeNotification(context: Context, eventTag: String?, plantName: String?) {
        createNotificationChannel(context)
        val notificationBuilder = NotificationCompat.Builder(context, "PLANT_NOTIFICATION")
            .setContentText("$eventTag $plantName")
            .setContentTitle("Don't forget!")
            .setGroup(NOTIFICATION_GROUP + eventTag)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_flower)
            .build()
        NotificationManagerCompat.from(context).notify(0, notificationBuilder)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            val name = "Plant Events"
            val descriptionText = "Alarm event reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "PLANT_NOTIFICATION"
        private const val NOTIFICATION_GROUP = "com.anonlatte.florarium."
    }
}
