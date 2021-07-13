package com.anonlatte.florarium.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "plant_alarms")
@Parcelize
data class PlantAlarm(
    @PrimaryKey @ColumnInfo(name = "id")
    val requestId: Long = 0,
    var plantName: String,
    var eventTag: String,
    var interval: Long,
    var lastCare: Long?
) : Parcelable