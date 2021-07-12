package com.anonlatte.florarium.data.repository

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

    fun createPlant(plant: Plant): Long = plantDao.create(plant)
    suspend fun getPlants(): List<Plant> = plantDao.getPlants()
    fun updatePlant(plant: Plant): Int = plantDao.update(plant)
    fun deletePlants(plants: List<Plant>): Int = plantDao.deleteMultiple(plants)
    fun addSchedule(regularSchedule: RegularSchedule?, winterSchedule: WinterSchedule?) {
        db.runInTransaction {
            if (regularSchedule?.plantId != null) {
                regularScheduleDao.create(regularSchedule)
            }
            if (winterSchedule?.plantId != null) {
                winterScheduleDao.create(winterSchedule)
            }
        }
    }

    fun updateSchedule(regularSchedule: RegularSchedule?, winterSchedule: WinterSchedule?) {
        db.runInTransaction {
            if (regularSchedule?.plantId != null) {
                regularScheduleDao.update(regularSchedule)
            }
            if (winterSchedule?.plantId != null) {
                winterScheduleDao.update(winterSchedule)
            }
        }
    }

    suspend fun getRegularScheduleList() = regularScheduleDao.getSchedules()
    fun createPlantAlarm(plantAlarm: PlantAlarm): Long = plantAlarmDao.create(plantAlarm)
    fun getPlantsAlarms(): List<PlantAlarm> = plantAlarmDao.getPlantsAlarms()
}
