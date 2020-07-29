package com.anonlatte.florarium.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "regular_schedule")
data class RegularSchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val scheduleId: Long,
    val plantId: Long,
    var wateringInterval: Int? = 7, // In days
    var wateredAt: Long? = null,
    var fertilizingInterval: Int? = null,
    var fertilizedAt: Long? = null,
    var rotatingInterval: Int? = null,
    var rotatedAt: Long? = null
)
