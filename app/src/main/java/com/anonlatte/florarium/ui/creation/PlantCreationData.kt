package com.anonlatte.florarium.ui.creation

import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.RegularSchedule

data class PlantCreationData(
    val plant: Plant = Plant(),
    val schedule: RegularSchedule = RegularSchedule(),
)