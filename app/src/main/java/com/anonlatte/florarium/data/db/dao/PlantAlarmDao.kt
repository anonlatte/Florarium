package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.data.model.PlantAlarm

@Dao
interface PlantAlarmDao : BaseDao<PlantAlarm> {
    @Query("SELECT * FROM plant_alarms")
    fun getPlantsAlarms(): List<PlantAlarm>
}
