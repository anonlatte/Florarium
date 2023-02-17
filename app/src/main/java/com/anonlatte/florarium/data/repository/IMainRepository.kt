package com.anonlatte.florarium.data.repository

import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.PlantWithSchedule
import com.anonlatte.florarium.data.model.RegularSchedule

interface IMainRepository {
    suspend fun createPlant(
        plant: Plant,
        regularSchedule: RegularSchedule,
    )

    suspend fun getPlants(): List<Plant>
    suspend fun updatePlant(plant: Plant): Int
    suspend fun deletePlants(plants: List<Plant>): Int
    suspend fun updateSchedule(regularSchedule: RegularSchedule?)
    suspend fun getRegularScheduleList(): List<RegularSchedule>
    suspend fun createPlantAlarm(plantAlarm: PlantAlarm): Long
    suspend fun getPlantsAlarms(): List<PlantAlarm>
    suspend fun getPlantsToSchedules(): List<PlantWithSchedule>
}
