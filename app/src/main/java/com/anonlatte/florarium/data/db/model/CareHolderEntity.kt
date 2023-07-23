package com.anonlatte.florarium.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anonlatte.florarium.data.domain.CareHolder

/**
 * Keeps track of the last time the plant was watered, sprayed, fertilized and rotated.
 */
@Entity(tableName = "care_holder")
data class CareHolderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "wateredAt")
    val wateredAt: Long = 0,
    @ColumnInfo(name = "sprayedAt")
    val sprayedAt: Long = 0,
    @ColumnInfo(name = "fertilizedAt")
    val fertilizedAt: Long = 0,
    @ColumnInfo(name = "rotatedAt")
    val rotatedAt: Long = 0,
) {

    companion object {
        fun CareHolderEntity.toDomain() = CareHolder(
            id = id,
            wateredAt = wateredAt,
            sprayedAt = sprayedAt,
            fertilizedAt = fertilizedAt,
            rotatedAt = rotatedAt,
        )
    }
}