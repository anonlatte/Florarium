package com.anonlatte.florarium.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.anonlatte.florarium.app.utils.getDaysFromTimestampAgo
import com.anonlatte.florarium.data.model.PlantAlarm

fun PlantAlarm.setAlarm(context: Context, intent: Intent, alarmManager: AlarmManager?) {
    val formattedInterval = interval * AlarmManager.INTERVAL_DAY
    val careLeftDays = countCareLeftDays(lastCare, formattedInterval)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestId.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    if (alarmManager != null) {
        alarmManager.cancel(pendingIntent)
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            careLeftDays,
            formattedInterval,
            pendingIntent
        )
    }
}

private fun countCareLeftDays(
    lastCare: Long?, formattedInterval: Long
): Long = if (lastCare != null) {
    val value = formattedInterval - getDaysFromTimestampAgo(lastCare) *
        AlarmManager.INTERVAL_DAY
    if (value < 1) {
        0
    } else {
        value
    }
} else {
    System.currentTimeMillis() + formattedInterval
}
