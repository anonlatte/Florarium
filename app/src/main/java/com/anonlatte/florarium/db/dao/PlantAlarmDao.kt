package com.anonlatte.florarium.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.db.models.PlantAlarm

@Dao
interface PlantAlarmDao : BaseDao<PlantAlarm> {
    @Query("SELECT * FROM plant_alarms WHERE id=:requestId")
    fun getPlantAlarm(requestId: Int): PlantAlarm

    @Query("SELECT * FROM plant_alarms")
    fun getPlantsAlarms(): List<PlantAlarm>
}
