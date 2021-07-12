package com.anonlatte.florarium.data.model

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
    val plantId: Long? = null,
    val wateringInterval: Int? = 10, // In days
    val sprayingInterval: Int? = null,
    val fertilizingInterval: Int? = null,
    val rotatingInterval: Int? = null
)
