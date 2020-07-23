package com.anonlatte.florarium.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anonlatte.florarium.db.AppDatabase.Companion.DB_VERSION
import com.anonlatte.florarium.db.dao.PlantDao
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.utilities.DATABASE_NAME

@Database(entities = [Plant::class], version = DB_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        const val DB_VERSION = 1
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}