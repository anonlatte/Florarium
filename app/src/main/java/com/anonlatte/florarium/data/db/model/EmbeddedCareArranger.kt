package com.anonlatte.florarium.data.db.model

import androidx.room.Embedded
import androidx.room.Relation

data class EmbeddedCareArranger(
    @Embedded
    val careArranger: CareArrangerEntity,
    @Relation(
        parentColumn = "plantId",
        entityColumn = "id",
    )
    val plant: PlantEntity,
    @Relation(
        parentColumn = "regularScheduleId",
        entityColumn = "id",
    )
    val regularSchedule: RegularScheduleEntity,
    @Relation(
        parentColumn = "careHolderId",
        entityColumn = "id",
    )
    val careHolder: CareHolderEntity,
)