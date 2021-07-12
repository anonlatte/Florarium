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
) {

    suspend fun createPlant(plant: Plant): Long = plantDao.create(plant)
    suspend fun getPlants(): List<Plant> = plantDao.getPlants()
    suspend fun updatePlant(plant: Plant): Int = plantDao.update(plant)
    suspend fun deletePlants(plants: List<Plant>): Int = plantDao.deleteMultiple(plants)
    suspend fun addSchedule(regularSchedule: RegularSchedule?, winterSchedule: WinterSchedule?) {
        db.withTransaction {
            if (regularSchedule?.plantId != null) {
                regularScheduleDao.create(regularSchedule)
            }
            if (winterSchedule?.plantId != null) {
                winterScheduleDao.create(winterSchedule)
            }
        }
    }

    suspend fun updateSchedule(regularSchedule: RegularSchedule?, winterSchedule: WinterSchedule?) {
        db.withTransaction {
            if (regularSchedule?.plantId != null) {
                regularScheduleDao.update(regularSchedule)
            }
            if (winterSchedule?.plantId != null) {
                winterScheduleDao.update(winterSchedule)
            }
        }
    }

    suspend fun getRegularScheduleList() = regularScheduleDao.getSchedules()
    suspend fun createPlantAlarm(plantAlarm: PlantAlarm): Long = plantAlarmDao.create(plantAlarm)
    suspend fun getPlantsAlarms(): List<PlantAlarm> = plantAlarmDao.getPlantsAlarms()
}
