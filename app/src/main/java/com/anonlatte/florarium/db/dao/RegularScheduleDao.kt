package com.anonlatte.florarium.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.db.models.RegularSchedule

@Dao
interface RegularScheduleDao : BaseDao<RegularSchedule> {
    @Query("SELECT * FROM regular_schedule")
    suspend fun getSchedules(): List<RegularSchedule>
}
