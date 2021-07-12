package com.anonlatte.florarium.db.models

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anonlatte.florarium.utilities.TimeStampHelper
import kotlinx.parcelize.Parcelize

@Entity(tableName = "plant_alarms")
@Parcelize
class PlantAlarm(
    @PrimaryKey @ColumnInfo(name = "id")
    val requestId: Long = 0,
    var plantName: String,
    var eventTag: String,
    var interval: Long,
    var lastCare: Long?
) : Parcelable {
    fun setAlarm(context: Context, intent: Intent, alarmManager: AlarmManager?) {
        val formattedInterval = interval * AlarmManager.INTERVAL_DAY
        val careLeftDays = if (lastCare != null) {
            val value =
                formattedInterval -
                    TimeStampHelper.getDaysFromTimestampAgo(lastCare) * AlarmManager.INTERVAL_DAY
            if (value < 1) {
                0
            } else {
                value
            }
        } else {
            System.currentTimeMillis() + formattedInterval
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestId.toInt(),
            intent,
            0
        )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
        alarmManager?.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            careLeftDays,
            formattedInterval,
            pendingIntent
        )
    }
}
