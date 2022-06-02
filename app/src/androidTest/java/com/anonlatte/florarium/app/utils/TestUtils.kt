package com.anonlatte.florarium.app.utils

import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.RegularSchedule
import org.jetbrains.annotations.TestOnly
import java.util.Date

@TestOnly
val testPlants = arrayListOf(
    Plant(plantId = 1, name = "rose", plantedAt = Date().time),
    Plant(plantId = 2, name = "lavender", plantedAt = Date().time),
    Plant(plantId = 3, name = "cactus", plantedAt = Date().time)
)

@TestOnly
val testRegularSchedules = arrayListOf(
    RegularSchedule(1, wateringInterval = 7),
    RegularSchedule(2, wateringInterval = 7, sprayingInterval = 7),
    RegularSchedule(3, wateringInterval = 7, sprayingInterval = 7, fertilizingInterval = 15),
    RegularSchedule(
        4,
        wateringInterval = 7,
        sprayingInterval = 7,
        fertilizingInterval = 15,
        rotatingInterval = 15
    )
)


@TestOnly
val testPlantAlarm = arrayListOf(
    PlantAlarm(1, "rose", "tag_1", 7, System.currentTimeMillis()),
    PlantAlarm(2, "lavender", "tag_2", 7, System.currentTimeMillis()),
    PlantAlarm(3, "cactus", "tag_3", 7, System.currentTimeMillis())
)
