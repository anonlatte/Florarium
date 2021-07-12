package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.data.model.RegularSchedule

@Dao
interface RegularScheduleDao : BaseDao<RegularSchedule> {
    @Query("SELECT * FROM regular_schedule")
    suspend fun getSchedules(): List<RegularSchedule>
}
