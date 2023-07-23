package com.anonlatte.florarium.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Keeps track of the last time the plant was watered, sprayed, fertilized and rotated.
 */
@Entity(
    tableName = "care_arranger",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RegularScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["regularScheduleId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CareHolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["careHolderId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["plantId"]),
        Index(value = ["regularScheduleId"]),
        Index(value = ["careHolderId"]),
    ]
)
data class CareArrangerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "plantId")
    val plantId: Long = 0,
    @ColumnInfo(name = "regularScheduleId")
    val regularScheduleId: Long = 0,
    @ColumnInfo(name = "careHolderId")
    val careHolderId: Long = 0,
)

