package com.anonlatte.florarium.ui.creation

import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.RegularSchedule

data class PlantCreationData(
    val plant: Plant = Plant(),
    val schedule: RegularSchedule = RegularSchedule(),
    val careHolder: CareHolder = CareHolder(),
)