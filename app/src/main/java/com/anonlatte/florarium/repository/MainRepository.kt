package com.anonlatte.florarium.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.anonlatte.florarium.db.AppDatabase
import com.anonlatte.florarium.db.dao.PlantDao
import com.anonlatte.florarium.db.dao.RegularScheduleDao
import com.anonlatte.florarium.db.dao.WinterScheduleDao
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.db.models.RegularSchedule
import com.anonlatte.florarium.db.models.WinterSchedule

class MainRepository(application: Application) {

    private val plantDao: PlantDao
    private val regularScheduleDao: RegularScheduleDao
    private val winterScheduleDao: WinterScheduleDao
    private val db = AppDatabase.getInstance(application)

    init {
        plantDao = db.plantDao()
        regularScheduleDao = db.regularScheduleDao()
        winterScheduleDao = db.winterScheduleDao()
    }

    fun createPlant(plant: Plant): Long = plantDao.createPlant(plant)

    fun createPlants(plants: List<Plant>): List<Long> = plantDao.createPlants(plants)

    fun getPlant(plantId: Long): LiveData<Plant> = plantDao.getPlant(plantId)

    fun getPlants(): LiveData<List<Plant>> = plantDao.getPlants()

    fun updatePlant(plant: Plant): Int = plantDao.updatePlant(plant)

    fun deletePlant(plant: Plant): Int = plantDao.deletePlant(plant)

    fun deletePlants(plants: List<Plant>): Int = plantDao.deletePlants(plants)

    fun addSchedule(regularSchedule: RegularSchedule?, winterSchedule: WinterSchedule?) {
        db.runInTransaction {
            if (regularSchedule?.plantId != null) {
                regularScheduleDao.createSchedule(regularSchedule)
            }
            if (winterSchedule?.plantId != null) {
                winterScheduleDao.createSchedule(winterSchedule)
            }
        }
    }
}
