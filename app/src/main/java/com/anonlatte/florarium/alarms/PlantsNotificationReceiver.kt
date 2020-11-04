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
import com.anonlatte.florarium.utilities.PLANT_NOTIFICATION_EVENT
import timber.log.Timber

class PlantsNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == PLANT_NOTIFICATION_EVENT) {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
            intent.extras?.getParcelable<PlantAlarm>("alarm")?.let { alarm ->
                makeNotification(context, alarm.eventTag, alarm.plantName)
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
            .setSmallIcon(R.drawable.sunflower)
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
