package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.data.db.model.PlantEntity

@Dao
interface PlantDao : BaseDao<PlantEntity> {
    @Query("SELECT * FROM plants ORDER BY name")
    suspend fun getPlants(): List<PlantEntity>
}
