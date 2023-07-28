package com.anonlatte.florarium.app.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.anonlatte.florarium.BuildConfig
import com.anonlatte.florarium.R
import com.anonlatte.florarium.data.domain.PlantWithSchedule
import com.anonlatte.florarium.data.repository.IMainRepository
import com.anonlatte.florarium.extensions.StringExt.capitalizeFirstLetter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class PlantsNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: IMainRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        val now = System.currentTimeMillis()
        val plantsToSchedules = repository.getPlantsToSchedules()
            .filter { it.getRequiredCareTypes(now).isNotEmpty() }

        when {
            plantsToSchedules.isEmpty() -> {
                Timber.d("No plants to water.")
            }

            plantsToSchedules.count() == 1 -> {
                val plantWithSchedule = plantsToSchedules.first()
                makeNotification(plantWithSchedule)
            }

            else -> {
                makeNotification(plantsToSchedules)
            }
        }
        return Result.success()
    }

    private fun makeNotification(plantWithScheduleList: List<PlantWithSchedule>) {
        makeNotification(
            context,
            context.getString(
                R.string.notifications_care_multiple,
                plantWithScheduleList.size,
            ),
            context.getString(R.string.notifications_care_message)
        )
    }

    private fun makeNotification(plantWithSchedule: PlantWithSchedule) {
        makeNotification(
            context,
            context.getString(
                R.string.notifications_care_title,
                plantWithSchedule.plant.name.capitalizeFirstLetter()
            ),
            context.getString(R.string.notifications_care_message)
        )
    }

    private fun makeNotification(
        context: Context,
        notificationTitle: String,
        notificationMessage: String,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notifications_care_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notifications_care_channel_description)
            }
            notificationManager.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentText(notificationMessage)
            .setContentTitle(notificationTitle)
            .setGroup(NOTIFICATION_GROUP + NOTIFICATION_EVENT_TAG)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_app_notification)
            .build()
        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(0, notificationBuilder)
            return
        }
    }

    companion object {
        private const val CHANNEL_ID = "PLANT_CARE_NOTIFICATION"
        private const val NOTIFICATION_GROUP = "com.anonlatte.florarium."
        private const val NOTIFICATION_EVENT_TAG = "care.notification"
        const val WORKER_NAME = "PLANT_NOTIFICATION_WORKER"

        fun init(context: Context, hours: Int, minutes: Int) {
            val periodicWorkRequestBuilder = dailyWorkRequest(hours, minutes)
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORKER_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                periodicWorkRequestBuilder
            )
        }

        private fun dailyWorkRequest(hours: Int, minutes: Int): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<PlantsNotificationWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(calculateNotificationDelay(hours, minutes), TimeUnit.MILLISECONDS)
                .build()
        }

        private fun calculateNotificationDelay(hours: Int, minutes: Int): Long {
            val currentTimeMillis = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val notificationTimeMillis = calendar.timeInMillis

            return if (notificationTimeMillis <= currentTimeMillis) {
                // If the notification time is in the past, schedule it for the next day
                notificationTimeMillis + TimeUnit.DAYS.toMillis(1)
            } else {
                notificationTimeMillis
            }
        }
    }
}