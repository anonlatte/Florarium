package com.anonlatte.florarium.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.anonlatte.florarium.db.AppDatabase
import com.anonlatte.florarium.db.dao.PlantAlarmDao
import com.anonlatte.florarium.db.dao.PlantDao
import com.anonlatte.florarium.db.dao.RegularScheduleDao
import com.anonlatte.florarium.db.dao.WinterScheduleDao
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.PlantAlarm
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.db.models.WinterSchedule

class MainRepository(context: Context) {

    private val plantAlarmDao: PlantAlarmDao
    private val plantDao: PlantDao
    private val regularScheduleDao: RegularScheduleDao
    private val winterScheduleDao: WinterScheduleDao
    private val db = AppDatabase.getInstance(context)

    init {
        plantAlarmDao = db.plantAlarmDao()
        plantDao = db.plantDao()
        regularScheduleDao = db.regularScheduleDao()
        winterScheduleDao = db.winterScheduleDao()
    }

    fun createPlant(plant: Plant): Long = plantDao.create(plant)
    fun getPlants(): LiveData<List<Plant>> = plantDao.getPlants()
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

    fun getRegularScheduleList() = regularScheduleDao.getSchedules()

    fun createPlantAlarm(plantAlarm: PlantAlarm): Long = plantAlarmDao.create(plantAlarm)
    fun getPlantsAlarms(): List<PlantAlarm> = plantAlarmDao.getPlantsAlarms()
}
