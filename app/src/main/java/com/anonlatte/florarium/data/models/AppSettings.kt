package com.anonlatte.florarium.data.models

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class AppSettings(
    val notificationHour: Int,
    val notificationMinute: Int,
) {

    companion object {

        val PREFS_NOTIFICATION_HOUR = intPreferencesKey("prefs_notification_hour")
        val PREFS_NOTIFICATION_MINUTES = intPreferencesKey("prefs_notification_minutes")

        fun Flow<Preferences>.toAppSettings(): Flow<AppSettings> = map {
            AppSettings(
                notificationHour = it[PREFS_NOTIFICATION_HOUR] ?: 0,
                notificationMinute = it[PREFS_NOTIFICATION_MINUTES] ?: 0,
            )
        }
    }
}