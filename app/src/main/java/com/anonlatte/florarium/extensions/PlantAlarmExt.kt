package com.anonlatte.florarium.extensions

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.anonlatte.florarium.app.service.PlantsNotificationWorker
import com.anonlatte.florarium.data.model.PlantAlarm
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import timber.log.Timber
import java.util.concurrent.TimeUnit

const val WORKER_ALARM_DATA_KEY = "alarm"

@SuppressLint("RestrictedApi")
fun PlantAlarm.setAlarm(
    context: Context,
    plantAlarm: PlantAlarm
) {
    val alarmFormattedToJson = Json.encodeToString(serializer(), plantAlarm)
    val alarmWorkerData = Data.Builder()
        .put(WORKER_ALARM_DATA_KEY, alarmFormattedToJson)
        .build()

    val periodicWorkRequestBuilder = PeriodicWorkRequestBuilder<PlantsNotificationWorker>(
        interval,
        TimeUnit.DAYS
    )
        .setInputData(alarmWorkerData)
        .setInitialDelay(interval, TimeUnit.DAYS)
        .build()

    Timber.d("Set repeating alarm for each $interval day")

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        requestId.toString(),
        ExistingPeriodicWorkPolicy.REPLACE,
        periodicWorkRequestBuilder
    )
}