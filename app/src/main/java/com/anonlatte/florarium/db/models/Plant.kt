package com.anonlatte.florarium.db.models

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "plants",
    foreignKeys = [ForeignKey(
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
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val plantId: Long,
    var name: String,
    var regularScheduleId: Long? = null,
    var winterScheduleId: Long? = null,
    var imageUrl: String? = null,
    var temperature: Double? = null, // In celsius
    var humidity: Double? = null, // In percents
    var soilAcidity: Double? = null, // pH
    var lighting: Double? = null, // Lumen
    var plantedAt: Long
) {
    @Ignore
    var regularSchedule: RegularSchedule? = null

    @Ignore
    var winterSchedule: WinterSchedule? = null
}