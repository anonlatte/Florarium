package com.anonlatte.florarium.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plants",
    foreignKeys = [
        ForeignKey(
            entity = RegularSchedule::class,
            parentColumns = ["id"],
            childColumns = ["regularScheduleId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ), ForeignKey(
            entity = WinterSchedule::class,
            parentColumns = ["id"],
            childColumns = ["winterScheduleId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index("regularScheduleId"), Index("winterScheduleId")]
)
data class Plant(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val plantId: Long? = 0,
    var name: String? = null,
    var regularScheduleId: Long? = null,
    var winterScheduleId: Long? = null,
    var imageUrl: String? = null,
    var temperature: Double? = null, // In celsius
    var humidity: Double? = null, // In percents
    var soilAcidity: Double? = null, // pH
    var lighting: Double? = null, // Lumen
    var plantedAt: Long? = null
) {
    @Ignore
    var regularSchedule: RegularSchedule? = null

    @Ignore
    var winterSchedule: WinterSchedule? = null
}
