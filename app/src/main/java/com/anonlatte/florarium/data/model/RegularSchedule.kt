package com.anonlatte.florarium.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

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
@Parcelize
data class RegularSchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val scheduleId: Long = 0,
    val plantId: Long? = null,
    val wateringInterval: Int? = 7, // In days
    val wateredAt: Long? = null,
    val sprayingInterval: Int? = null,
    val sprayedAt: Long? = null,
    val fertilizingInterval: Int? = null,
    val fertilizedAt: Long? = null,
    val rotatingInterval: Int? = null,
    val rotatedAt: Long? = null
) : Parcelable