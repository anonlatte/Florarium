package com.anonlatte.florarium.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.db.models.WinterSchedule

@Dao
interface WinterScheduleDao : BaseDao<WinterSchedule> {
    @Query("SELECT * FROM winter_schedule WHERE plantId=:plantId")
    fun getSchedule(plantId: Long): LiveData<WinterSchedule>

    @Query("SELECT * FROM winter_schedule")
    fun getSchedules(): LiveData<List<WinterSchedule>>
}
