package com.anonlatte.florarium.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "plants")
@Parcelize
data class Plant(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val plantId: Long = 0,
    var name: String? = null,
    var imageUrl: String? = null,
    var temperature: Double? = null, // In celsius
    var humidity: Double? = null, // In percents
    var soilAcidity: Double? = null, // pH
    var lighting: Double? = null, // Lumen
    var plantedAt: Long? = null
) : Parcelable