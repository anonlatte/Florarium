package com.anonlatte.florarium.alarms

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anonlatte.florarium.repository.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class RestartAlarmsService : BroadcastReceiver() {

    companion object {
        const val ACTION_NAME = "PLANT_EVENT"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            if (context == null) {
                return
            }
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val mainRepository = MainRepository(context)
            CoroutineScope(Dispatchers.IO).launch {
                val plantsAlarms = mainRepository.getPlantsAlarms()
                plantsAlarms.forEach { plantAlarm ->
                    val plantsAlarmIntent =
                        Intent(context, PlantsNotificationReceiver::class.java).apply {
                            action = ACTION_NAME
                            putExtra("alarm", plantAlarm)
                        }
                    plantAlarm.setAlarm(
                        context,
                        plantsAlarmIntent,
                        alarmManager
                    )
                }
                Timber.tag("alarm").d("had been set ${plantsAlarms.size} alarms.")
            }
        }
    }
}
