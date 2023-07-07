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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val scheduleId: Long = 0,
    val plantId: Long? = null,
    /** In days */
    val wateringInterval: Int = 7,
    val wateredAt: Long? = null,
    val sprayingInterval: Int = 0,
    val sprayedAt: Long? = null,
    val fertilizingInterval: Int = 0,
    val fertilizedAt: Long? = null,
    val rotatingInterval: Int = 0,
    val rotatedAt: Long? = null,
) : Parcelable