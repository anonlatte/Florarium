package com.anonlatte.florarium.utilities

import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.PlantAlarm
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.db.models.WinterSchedule
import java.util.Date
import org.jetbrains.annotations.TestOnly

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
val testWinterSchedules = arrayListOf(
    WinterSchedule(1, wateringInterval = 7),
    WinterSchedule(2, wateringInterval = 7, sprayingInterval = 7),
    WinterSchedule(3, wateringInterval = 7, sprayingInterval = 7, fertilizingInterval = 15),
    WinterSchedule(
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
    PlantAlarm(3, "cactus", "tag_3", 7, System.currentTimeMillis()),
)
