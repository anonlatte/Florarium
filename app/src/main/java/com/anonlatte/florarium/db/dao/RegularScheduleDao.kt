package com.anonlatte.florarium.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.db.models.RegularSchedule

@Dao
interface RegularScheduleDao : BaseDao<RegularSchedule> {
    @Query("SELECT * FROM regular_schedule WHERE plantId=:plantId")
    fun getSchedule(plantId: Long): LiveData<RegularSchedule>

    @Query("SELECT * FROM regular_schedule")
    fun getSchedules(): LiveData<List<RegularSchedule>>
}
