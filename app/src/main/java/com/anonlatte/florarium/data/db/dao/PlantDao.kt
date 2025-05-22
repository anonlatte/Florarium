package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.data.db.model.PlantEntity

@Dao
interface PlantDao : BaseDao<PlantEntity> {
    @Query("SELECT * FROM plants ORDER BY name")
    suspend fun getPlants(): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Long): PlantEntity?

    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deletePlant(id: Long)

    @Query("UPDATE plants SET name = :name, imageUri = :imageUri WHERE id = :id")
    suspend fun updatePlant(id: Long, name: String, imageUri: String)
}
