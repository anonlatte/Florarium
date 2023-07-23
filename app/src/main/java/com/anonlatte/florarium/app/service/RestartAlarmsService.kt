package com.anonlatte.florarium.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.anonlatte.florarium.data.models.AppSettings.Companion.toAppSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RestartAlarmsService @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == ACTION_BOOT_QUICKBOOT_POWERON
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                val appSettings = preferencesDataStore.data.toAppSettings().first()
                PlantsNotificationWorker.init(
                    context,
                    appSettings.notificationHour,
                    appSettings.notificationMinute
                )
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val ACTION_BOOT_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}