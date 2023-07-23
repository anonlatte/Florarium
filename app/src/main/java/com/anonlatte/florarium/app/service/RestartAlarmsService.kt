package com.anonlatte.florarium.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestartAlarmsService : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == ACTION_BOOT_QUICKBOOT_POWERON
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                // get convenient notification time from dataStore
                PlantsNotificationWorker.init(context, 0, 0)
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val ACTION_BOOT_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
