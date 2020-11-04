package com.anonlatte.florarium.db.models

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anonlatte.florarium.utilities.TimeStampHelper

@Entity(tableName = "plant_alarms")
class PlantAlarm(
    @PrimaryKey @ColumnInfo(name = "id")
    val requestId: Long = 0,
    var plantName: String,
    var eventTag: String,
    var interval: Long,
    var lastCare: Long?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(requestId)
        parcel.writeString(plantName)
        parcel.writeString(eventTag)
        parcel.writeLong(interval)
        parcel.writeValue(lastCare)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlantAlarm> {
        override fun createFromParcel(parcel: Parcel): PlantAlarm {
            return PlantAlarm(parcel)
        }

        override fun newArray(size: Int): Array<PlantAlarm?> {
            return arrayOfNulls(size)
        }
    }
}
