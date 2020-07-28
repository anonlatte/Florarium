package com.anonlatte.florarium.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "winter_schedule")
data class WinterSchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val scheduleId: Long,
    val plantId: Long,
    var wateringInterval: Int? = 10, // In days
    var wateredAt: Long? = null,
    var fertilizingInterval: Int? = null,
    var fertilizedAt: Long? = null,
    var rotatingInterval: Int? = null,
    var rotatedAt: Long? = null
)