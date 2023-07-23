package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import com.anonlatte.florarium.data.db.model.CareArrangerEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class CareArranger(
    val id: Long = 0,
    val plant: Plant,
    val regularSchedule: RegularSchedule,
    val careHolder: CareHolder,
) : Parcelable {

    companion object {
        fun CareArranger.toEntity() = CareArrangerEntity(
            id = id,
            plantId = plant.id,
            regularScheduleId = regularSchedule.id,
            careHolderId = careHolder.id,
        )
    }
}