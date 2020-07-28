package com.anonlatte.florarium.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anonlatte.florarium.db.models.WinterSchedule

@Dao
interface WinterScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createSchedule(winterSchedule: WinterSchedule): Long

    @Query("SELECT * FROM winter_schedule WHERE plantId=:plantId")
    fun getSchedule(plantId: Long): LiveData<WinterSchedule>

    @Query("SELECT * FROM winter_schedule")
    fun getSchedules(): LiveData<List<WinterSchedule>>

    @Update
    fun updateSchedule(winterSchedule: WinterSchedule): Int

    @Delete
    fun deleteSchedule(winterSchedule: WinterSchedule): Int
}
