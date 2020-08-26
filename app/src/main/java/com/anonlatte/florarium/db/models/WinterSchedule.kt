package com.anonlatte.florarium.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "winter_schedule",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
data class WinterSchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val scheduleId: Long = 0,
    var plantId: Long? = null,
    var wateringInterval: Int? = 10, // In days
    var sprayingInterval: Int? = null,
    var fertilizingInterval: Int? = null,
    var rotatingInterval: Int? = null
)
