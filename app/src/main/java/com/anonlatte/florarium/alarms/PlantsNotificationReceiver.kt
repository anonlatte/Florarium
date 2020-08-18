package com.anonlatte.florarium.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anonlatte.florarium.BuildConfig
import com.anonlatte.florarium.R
import com.anonlatte.florarium.db.models.PlantAlarm
import timber.log.Timber

class PlantsNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, RestartAlarmsService::class.java))
            } else {
                context.startService(Intent(context, RestartAlarmsService::class.java))
            }
        }
        if (intent.action == "PLANT_EVENT") {
            intent.extras?.getParcelable<PlantAlarm>("alarm")?.run {
                makeNotification(context, eventTag, plantName)
                Timber.tag("alarm").d("notified $eventTag:$plantName")
            }
        }
    }

    private fun makeNotification(context: Context, eventTag: String?, plantName: String?) {
        createNotificationChannel(context)
        val notificationBuilder = NotificationCompat.Builder(context, "PLANT_NOTIFICATION")
            .setContentText("$eventTag $plantName")
            .setContentTitle("Don't forget!")
            .setGroup(NOTIFICATION_GROUP + eventTag)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        NotificationManagerCompat.from(context).notify(0, notificationBuilder)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        const val CHANNEL_ID = "PLANT_NOTIFICATION"
        const val NOTIFICATION_GROUP = "com.anonlatte.florarium."
    }
}
