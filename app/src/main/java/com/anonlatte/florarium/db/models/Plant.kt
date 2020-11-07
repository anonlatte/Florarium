package com.anonlatte.florarium.db.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val plantId: Long = 0,
    var name: String? = null,
    var imageUrl: String? = null,
    var temperature: Double? = null, // In celsius
    var humidity: Double? = null, // In percents
    @ColumnInfo(name = "soil_acidity")
    var soilAcidity: Double? = null, // pH
    var lighting: Double? = null, // Lumen
    @ColumnInfo(name = "planted_at")
    var plantedAt: Long? = null,
    @ColumnInfo(name = "updated_at")
    var updatedAt: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(plantId)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeValue(temperature)
        parcel.writeValue(humidity)
        parcel.writeValue(soilAcidity)
        parcel.writeValue(lighting)
        parcel.writeValue(plantedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Plant> {
        override fun createFromParcel(parcel: Parcel): Plant {
            return Plant(parcel)
        }

        override fun newArray(size: Int): Array<Plant?> {
            return arrayOfNulls(size)
        }
    }
}
