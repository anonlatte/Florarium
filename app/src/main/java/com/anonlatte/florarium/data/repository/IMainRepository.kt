package com.anonlatte.florarium.data.repository

import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.PlantWithSchedule
import com.anonlatte.florarium.data.domain.RegularSchedule

interface IMainRepository {
    suspend fun createPlant(
        plant: Plant,
        regularSchedule: RegularSchedule,
        careHolder: CareHolder,
    )

    suspend fun getPlants(): List<Plant>
    suspend fun updatePlant(plant: Plant): Int
    suspend fun deletePlants(plants: List<Plant>): Int
    suspend fun updateSchedule(regularSchedule: RegularSchedule?)
    suspend fun getRegularScheduleList(): List<RegularSchedule>
    suspend fun getPlantsToSchedules(): List<PlantWithSchedule>
    suspend fun updateGlobalNotificationTime(hour: Int, minute: Int)
}
