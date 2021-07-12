package com.anonlatte.florarium.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anonlatte.florarium.data.db.AppDatabase.Companion.DB_VERSION
import com.anonlatte.florarium.data.db.dao.PlantAlarmDao
import com.anonlatte.florarium.data.db.dao.PlantDao
import com.anonlatte.florarium.data.db.dao.RegularScheduleDao
import com.anonlatte.florarium.data.db.dao.WinterScheduleDao
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantAlarm
import com.anonlatte.florarium.data.model.RegularSchedule
import com.anonlatte.florarium.data.model.WinterSchedule

@Database(
    entities = [Plant::class, RegularSchedule::class, WinterSchedule::class, PlantAlarm::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun plantAlarmDao(): PlantAlarmDao
    abstract fun plantDao(): PlantDao
    abstract fun regularScheduleDao(): RegularScheduleDao
    abstract fun winterScheduleDao(): WinterScheduleDao

    companion object {
        const val DB_VERSION = 1
    }
}
