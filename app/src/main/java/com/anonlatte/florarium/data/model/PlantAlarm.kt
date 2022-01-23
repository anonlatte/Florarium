package com.anonlatte.florarium.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "plant_alarms")
@Parcelize
@Serializable
data class PlantAlarm(
    @PrimaryKey @ColumnInfo(name = "id")
    val requestId: Long = 0,
    @ColumnInfo(name = "plantName")
    val plantName: String,
    @ColumnInfo(name = "eventTag")
    val eventTag: String,
    @ColumnInfo(name = "interval")
    val interval: Long,
    @ColumnInfo(name = "lastCare")
    val lastCare: Long?
) : Parcelable