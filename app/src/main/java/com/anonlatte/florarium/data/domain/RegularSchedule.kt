package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import com.anonlatte.florarium.data.db.model.RegularScheduleEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegularSchedule(
    val id: Long = 0,
    val wateringInterval: Int = 7,
    val sprayingInterval: Int = 0,
    val fertilizingInterval: Int = 0,
    val rotatingInterval: Int = 0,
) : Parcelable {

    companion object {


        fun RegularSchedule.toEntity(): RegularScheduleEntity {
            return RegularScheduleEntity(
                id = id,
                wateringInterval = wateringInterval,
                sprayingInterval = sprayingInterval,
                fertilizingInterval = fertilizingInterval,
                rotatingInterval = rotatingInterval,
            )
        }
    }
}