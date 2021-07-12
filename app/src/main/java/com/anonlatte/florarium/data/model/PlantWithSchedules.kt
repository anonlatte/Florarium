package com.anonlatte.florarium.data.model

data class PlantWithSchedule(
    val plant: Plant,
    val schedule: RegularSchedule?
)