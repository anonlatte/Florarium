package com.anonlatte.florarium.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anonlatte.florarium.data.domain.Plant

/**
 * @param id id of the plant
 * @param name name of the plant
 * @param imageUri uri of the plant's image
 * @param createdAt date when the plant was planted
 */
@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String = "",
    @ColumnInfo(name = "imageUri")
    val imageUri: String = "",
    @ColumnInfo(name = "createdAt")
    val createdAt: Long? = null,
) {

    companion object {
        fun PlantEntity.toDomain() = Plant(
            id = id,
            name = name,
            imageUri = imageUri,
            createdAt = createdAt,
        )
    }
}