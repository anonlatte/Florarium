package com.anonlatte.florarium.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anonlatte.florarium.data.db.AppDatabase.Companion.DB_VERSION
import com.anonlatte.florarium.data.db.dao.CareArrangerDao
import com.anonlatte.florarium.data.db.dao.CareHolderDao
import com.anonlatte.florarium.data.db.dao.PlantDao
import com.anonlatte.florarium.data.db.dao.RegularScheduleDao
import com.anonlatte.florarium.data.db.model.CareArrangerEntity
import com.anonlatte.florarium.data.db.model.CareHolderEntity
import com.anonlatte.florarium.data.db.model.PlantEntity
import com.anonlatte.florarium.data.db.model.RegularScheduleEntity

@Database(
    entities = [PlantEntity::class, RegularScheduleEntity::class, CareArrangerEntity::class, CareHolderEntity::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao
    abstract fun regularScheduleDao(): RegularScheduleDao
    abstract fun careArrangerDao(): CareArrangerDao
    abstract fun careHolderDao(): CareHolderDao

    companion object {
        const val DB_VERSION = 1
    }
}