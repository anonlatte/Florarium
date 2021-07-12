package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.data.model.Plant

@Dao
interface PlantDao : BaseDao<Plant> {
    @Query("SELECT * FROM plants ORDER BY name")
    suspend fun getPlants(): List<Plant>
}
