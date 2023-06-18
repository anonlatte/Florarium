package com.anonlatte.florarium.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "plants")
@Parcelize
/**
 * @param plantId id of the plant
 * @param name name of the plant
 * @param imageUri uri of the plant's image
 * @param temperature temperature of the plant in celsius
 * @param humidity humidity of the plant in percents
 * @param soilAcidity soil acidity of the plant in pH
 * @param lighting lighting of the plant in lumen
 * @param createdAt date when the plant was planted
 */
data class Plant(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val plantId: Long = 0,
    val name: String = "",
    val imageUri: String = "",
    val temperature: Double? = null,
    val humidity: Double? = null,
    val soilAcidity: Double? = null,
    val lighting: Double? = null,
    val createdAt: Long? = null
) : Parcelable