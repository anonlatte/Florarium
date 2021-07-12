package com.anonlatte.florarium.data.repository

import androidx.room.withTransaction
import com.anonlatte.florarium.data.db.AppDatabase
import com.anonlatte.florarium.data.db.dao.PlantAlarmDao
import com.anonlatte.florarium.data.db.dao.PlantDao
import com.anonlatte.florarium.data.db.dao.RegularScheduleDao
import com.anonlatte.florarium.data.db.dao.WinterScheduleDao
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.data.model.WinterSchedule
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val db: AppDatabase,
    private val plantAlarmDao: PlantAlarmDao,
    private val plantDao: PlantDao,
    private val regularScheduleDao: RegularScheduleDao,
    private val winterScheduleDao: WinterScheduleDao
) : IMainRepository {

    override suspend fun createPlant(plant: Plant): Long = plantDao.create(plant)
    override suspend fun getPlants(): List<Plant> = plantDao.getPlants()
    override suspend fun updatePlant(plant: Plant): Int = plantDao.update(plant)
    override suspend fun deletePlants(plants: List<Plant>): Int = plantDao.deleteMultiple(plants)
    override suspend fun addSchedule(
        regularSchedule: RegularSchedule?,
        winterSchedule: WinterSchedule?
    ) {
        db.withTransaction {
            if (regularSchedule?.plantId != null) {
                regularScheduleDao.create(regularSchedule)
            }
            if (winterSchedule?.plantId != null) {
                winterScheduleDao.create(winterSchedule)
            }
        }
    }

    override suspend fun updateSchedule(
        regularSchedule: RegularSchedule?,
        winterSchedule: WinterSchedule?
    ) {
        db.withTransaction {
            if (regularSchedule?.plantId != null) {
                regularScheduleDao.update(regularSchedule)
            }
            if (winterSchedule?.plantId != null) {
                winterScheduleDao.update(winterSchedule)
            }
        }
    }

    override suspend fun getRegularScheduleList() = regularScheduleDao.getSchedules()
    override suspend fun createPlantAlarm(plantAlarm: PlantAlarm): Long {
        return plantAlarmDao.create(plantAlarm)
    }

    override suspend fun getPlantsAlarms(): List<PlantAlarm> = plantAlarmDao.getPlantsAlarms()
}
