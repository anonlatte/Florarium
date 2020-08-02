package com.anonlatte.florarium.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "regular_schedule",
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
data class RegularSchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val scheduleId: Long? = 0,
    var plantId: Long? = null,
    var wateringInterval: Int? = 7, // In days
    var wateredAt: Long? = null,
    var sprayingInterval: Int? = null,
    var sprayedAt: Long? = null,
    var fertilizingInterval: Int? = null,
    var fertilizedAt: Long? = null,
    var rotatingInterval: Int? = null,
    var rotatedAt: Long? = null
)
