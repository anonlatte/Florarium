package com.anonlatte.florarium.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.anonlatte.florarium.db.AppDatabase
import com.anonlatte.florarium.db.dao.PlantDao
import com.anonlatte.florarium.db.models.Plant

object MainRepository {

    fun createPlant(plant: Plant): Long = plantDao.createPlant(plant)

    fun createPlants(plants: List<Plant>): List<Long> = plantDao.createPlants(plants)

    fun getPlant(plantId: Long): LiveData<Plant> = plantDao.getPlant(plantId)

    fun getPlants(): LiveData<List<Plant>> = plantDao.getPlants()

    fun updatePlant(plant: Plant): Int = plantDao.updatePlant(plant)

    fun deletePlant(plant: Plant): Int = plantDao.deletePlant(plant)

    fun deletePlants(plants: List<Plant>): Int = plantDao.deletePlants(plants)

    @Volatile
    private var instance: MainRepository? = null

    private lateinit var plantDao: PlantDao

    fun getRepository(application: Application): MainRepository {
        plantDao = AppDatabase.getInstance(application).plantDao()
        return instance ?: synchronized(this) {
            instance ?: MainRepository.also {
                instance = it
            }
        }
    }
}
