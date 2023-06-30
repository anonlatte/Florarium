package com.anonlatte.florarium.ui.creation

import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.RegularSchedule

sealed interface PlantCreationState {
    data class Default(
        val plant: Plant = Plant(),
        val schedule: RegularSchedule = RegularSchedule()
    ) : PlantCreationState
}