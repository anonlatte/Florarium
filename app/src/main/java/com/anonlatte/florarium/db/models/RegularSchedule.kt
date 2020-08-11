package com.anonlatte.florarium.db.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
data class RegularSchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val scheduleId: Long = 0,
    var plantId: Long? = null,
    var wateringInterval: Int? = 7, // In days
    var wateredAt: Long? = null,
    var sprayingInterval: Int? = null,
    var sprayedAt: Long? = null,
    var fertilizingInterval: Int? = null,
    var fertilizedAt: Long? = null,
    var rotatingInterval: Int? = null,
    var rotatedAt: Long? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(scheduleId)
        parcel.writeValue(plantId)
        parcel.writeValue(wateringInterval)
        parcel.writeValue(wateredAt)
        parcel.writeValue(sprayingInterval)
        parcel.writeValue(sprayedAt)
        parcel.writeValue(fertilizingInterval)
        parcel.writeValue(fertilizedAt)
        parcel.writeValue(rotatingInterval)
        parcel.writeValue(rotatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegularSchedule> {
        override fun createFromParcel(parcel: Parcel): RegularSchedule {
            return RegularSchedule(parcel)
        }

        override fun newArray(size: Int): Array<RegularSchedule?> {
            return arrayOfNulls(size)
        }
    }
}
