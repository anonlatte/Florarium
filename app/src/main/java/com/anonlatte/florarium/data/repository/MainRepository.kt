package com.anonlatte.florarium.data.repository

import com.anonlatte.florarium.data.db.dao.PlantAlarmDao
import com.anonlatte.florarium.data.db.dao.PlantDao
import com.anonlatte.florarium.data.db.dao.RegularScheduleDao
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.PlantWithSchedule
import com.anonlatte.florarium.data.model.RegularSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val plantAlarmDao: PlantAlarmDao,
    private val plantDao: PlantDao,
    private val regularScheduleDao: RegularScheduleDao,
) : IMainRepository {

    override suspend fun createPlant(
        plant: Plant,
        regularSchedule: RegularSchedule,
    ) {
        val plantId = plantDao.create(plant)
        regularScheduleDao.create(regularSchedule.copy(plantId = plantId))
    }

    override suspend fun getPlants(): List<Plant> = plantDao.getPlants()
    override suspend fun updatePlant(plant: Plant): Int = plantDao.update(plant)
    override suspend fun deletePlants(plants: List<Plant>): Int = plantDao.deleteMultiple(plants)
    override suspend fun updateSchedule(
        regularSchedule: RegularSchedule?,
    ) {
        if (regularSchedule?.plantId != null) {
            regularScheduleDao.update(regularSchedule)
        }
    }

    override suspend fun getRegularScheduleList() = regularScheduleDao.getSchedules()
    override suspend fun createPlantAlarm(plantAlarm: PlantAlarm): Long {
        return withContext(Dispatchers.IO) {
            plantAlarmDao.create(plantAlarm)
        }
    }

    override suspend fun getPlantsAlarms(): List<PlantAlarm> = plantAlarmDao.getPlantsAlarms()

    override suspend fun getPlantsToSchedules(): List<PlantWithSchedule> {
        val plantsList = getPlants()
        val schedulesList = getRegularScheduleList()
        return plantsList.map { plant ->
            val associatedSchedule = schedulesList.firstOrNull { it.plantId == plant.plantId }
            PlantWithSchedule(plant, associatedSchedule)
        }
    }
}
