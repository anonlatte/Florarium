package com.anonlatte.florarium.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.anonlatte.florarium.data.repository.IMainRepository
import com.anonlatte.florarium.extensions.setAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RestartAlarmsService : BroadcastReceiver() {

    @Inject
    lateinit var mainRepository: IMainRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == ACTION_BOOT_QUICKBOOT_POWERON
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                val plantsAlarms = mainRepository.getPlantsAlarms()
                plantsAlarms.forEach { plantAlarm ->
                    plantAlarm.setAlarm(context, plantAlarm)
                }
                Timber.d("Alarms had been set ${plantsAlarms.size} alarms.")
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val ACTION_BOOT_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
