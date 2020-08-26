package com.anonlatte.florarium.alarms

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.anonlatte.florarium.R
import com.anonlatte.florarium.repository.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class RestartAlarmsService : Service() {

    private var serviceNotification: NotificationManager? = null

    companion object {
        const val SERVICE_NOTIFICATION_ID = 2
        const val CHANNEL_ID = "PLANT_NOTIFICATION"
        const val ACTION_NAME = "PLANT_EVENT"
    }

    override fun onCreate() {
        super.onCreate()
        startMyOwnForeground()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val mainRepository = MainRepository(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val plantsAlarms = mainRepository.getPlantsAlarms()
            plantsAlarms.forEach { plantAlarm ->
                val plantsAlarmIntent =
                    Intent(applicationContext, RestartAlarmsService::class.java).apply {
                        action = ACTION_NAME
                        putExtra("alarm", plantAlarm)
                    }
                plantAlarm.setAlarm(
                    applicationContext,
                    plantsAlarmIntent,
                    alarmManager
                )
            }
            Timber.tag("alarm").d("had been set ${plantsAlarms.size} alarms.")
            serviceNotification?.cancel(SERVICE_NOTIFICATION_ID)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Alarm restoring"
            val chan = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            serviceNotification =
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            serviceNotification?.createNotificationChannel(chan)
            val notificationBuilder =
                NotificationCompat.Builder(this, CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(SERVICE_NOTIFICATION_ID, notification)
        }
    }
}
