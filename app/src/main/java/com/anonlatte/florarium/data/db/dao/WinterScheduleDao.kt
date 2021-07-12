package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.data.model.WinterSchedule

@Dao
interface WinterScheduleDao : BaseDao<WinterSchedule> {
    @Query("SELECT * FROM winter_schedule")
    suspend fun getSchedules(): List<WinterSchedule>
}
