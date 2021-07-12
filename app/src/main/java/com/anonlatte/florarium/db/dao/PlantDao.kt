package com.anonlatte.florarium.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.db.models.Plant

@Dao
interface PlantDao : BaseDao<Plant> {
    @Query("SELECT * FROM plants ORDER BY name")
    suspend fun getPlants(): List<Plant>
}
