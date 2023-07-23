package com.anonlatte.florarium.app.utils

import com.anonlatte.florarium.data.db.model.PlantEntity
import com.anonlatte.florarium.data.db.model.RegularScheduleEntity
import org.jetbrains.annotations.TestOnly
import java.util.Date

@TestOnly
val testPlants = arrayListOf(
    PlantEntity(id = 1, name = "rose", createdAt = Date().time),
    PlantEntity(id = 2, name = "lavender", createdAt = Date().time),
    PlantEntity(id = 3, name = "cactus", createdAt = Date().time)
)

@TestOnly
val testRegularSchedules = arrayListOf(
    RegularScheduleEntity(1, wateringInterval = 7),
    RegularScheduleEntity(2, wateringInterval = 7, sprayingInterval = 7),
    RegularScheduleEntity(3, wateringInterval = 7, sprayingInterval = 7, fertilizingInterval = 15),
    RegularScheduleEntity(
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
