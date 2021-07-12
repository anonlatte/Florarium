package com.anonlatte.florarium.data.model

import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule

data class PlantWithSchedule(
    val plant: Plant,
    val schedule: RegularSchedule
)