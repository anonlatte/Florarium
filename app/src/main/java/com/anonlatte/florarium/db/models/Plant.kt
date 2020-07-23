package com.anonlatte.florarium.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val plantId: Long,
    var name: String,
    var imageUrl: String? = null,
    var temperature: Double? = 22.0, // In celsius
    var humidity: Double? = 50.0, // In percents
    var soilAcidity: Double? = 7.0, // pH
    var lighting: Double? = 415.0,
    var wateringInterval: Int? = 7, // In days
    var fertilizingInterval: Int? = 30,
    var rotatingInterval: Int? = 15,
    var wateredAt: Long? = null,
    var fertilizedAt: Long? = null,
    var rotatedAt: Long? = null,
    var plantedAt: Long
)