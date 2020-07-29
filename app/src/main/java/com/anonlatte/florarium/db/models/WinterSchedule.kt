package com.anonlatte.florarium.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "winter_schedule")
data class WinterSchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val scheduleId: Long? = 0,
    val plantId: Long? = null,
    var wateringInterval: Int? = 10, // In days
    var sprayingInterval: Int? = null,
    var fertilizingInterval: Int? = null,
    var rotatingInterval: Int? = null
)
