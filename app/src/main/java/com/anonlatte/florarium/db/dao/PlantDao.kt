package com.anonlatte.florarium.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anonlatte.florarium.db.models.Plant

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createPlant(plant: Plant): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createPlants(plants: List<Plant>): List<Long>

    @Query("SELECT * FROM plants WHERE id=:plantId")
    fun getPlant(plantId: Long): LiveData<Plant>

    @Query("SELECT * FROM plants ORDER BY name")
    fun getPlants(): LiveData<List<Plant>>

    @Update
    fun updatePlant(plant: Plant): Int

    @Delete
    fun deletePlant(plant: Plant): Int

    @Delete
    fun deletePlants(plants: List<Plant>): Int
}
