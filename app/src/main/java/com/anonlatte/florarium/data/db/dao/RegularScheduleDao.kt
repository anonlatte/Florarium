package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.anonlatte.florarium.data.db.model.RegularScheduleEntity

@Dao
interface RegularScheduleDao : BaseDao<RegularScheduleEntity> {
    @Query("SELECT * FROM regular_schedule")
    suspend fun getSchedules(): List<RegularScheduleEntity>
}
