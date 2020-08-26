package com.anonlatte.florarium.utilities

import com.anonlatte.florarium.db.models.Plant
import java.util.Date

val testPlants = arrayListOf(
    Plant(plantId = 1, name = "rose", plantedAt = Date().time),
    Plant(plantId = 2, name = "lavender", plantedAt = Date().time),
    Plant(plantId = 3, name = "cactus", plantedAt = Date().time)
)
