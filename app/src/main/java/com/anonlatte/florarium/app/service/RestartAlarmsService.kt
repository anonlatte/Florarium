package com.anonlatte.florarium.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anonlatte.florarium.data.repository.MainRepository
import javax.inject.Inject

class RestartAlarmsService : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == ACTION_BOOT_QUICKBOOT_POWERON
        ) {
            val pendingResult = goAsync()
            // TODO restart alarms
            /*
                        CoroutineScope(Dispatchers.IO).launch {
                            val plantsAlarms = mainRepository.getPlantsAlarms()
                            plantsAlarms.forEach { plantAlarm ->
                                plantAlarm.setAlarm(context, plantAlarm)
                            }
                            Timber.d("Alarms had been set ${plantsAlarms.size} alarms.")
                            pendingResult.finish()
                        }
            */
        }
    }

    companion object {
        private const val ACTION_BOOT_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
