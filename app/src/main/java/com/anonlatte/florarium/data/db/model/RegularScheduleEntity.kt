package com.anonlatte.florarium.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anonlatte.florarium.data.domain.RegularSchedule

/**
 * @param id id of the schedule
 * @param wateringInterval interval of watering in days
 * @param sprayingInterval interval of spraying in days
 * @param fertilizingInterval interval of fertilizing in days
 * @param rotatingInterval interval of rotating in days
 */
@Entity(tableName = "regular_schedule")
data class RegularScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "wateringInterval")
    val wateringInterval: Int = 7,
    @ColumnInfo(name = "sprayingInterval")
    val sprayingInterval: Int = 0,
    @ColumnInfo(name = "fertilizingInterval")
    val fertilizingInterval: Int = 0,
    @ColumnInfo(name = "rotatingInterval")
    val rotatingInterval: Int = 0,
) {

    companion object {

        fun RegularScheduleEntity.toDomain() = RegularSchedule(
            id = id,
            wateringInterval = wateringInterval,
            sprayingInterval = sprayingInterval,
            fertilizingInterval = fertilizingInterval,
            rotatingInterval = rotatingInterval,
        )
    }
}