package com.anonlatte.florarium.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anonlatte.florarium.db.models.RegularSchedule

@Dao
interface RegularScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createSchedule(regularSchedule: RegularSchedule): Long

    @Query("SELECT * FROM regular_schedule WHERE plantId=:plantId")
    fun getSchedule(plantId: Long): LiveData<RegularSchedule>

    @Query("SELECT * FROM regular_schedule")
    fun getSchedules(): LiveData<List<RegularSchedule>>

    @Update
    fun updateSchedule(regularSchedule: RegularSchedule): Int

    @Delete
    fun deleteSchedule(regularSchedule: RegularSchedule): Int
}
