package com.anonlatte.florarium.di.module

import android.content.Context
import androidx.room.Room
import com.anonlatte.florarium.app.utils.DATABASE_NAME
import com.anonlatte.florarium.data.db.AppDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    @Provides
    fun provideDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context, AppDatabase::class.java, DATABASE_NAME
    ).build()

    @Provides
    fun providePlantDao(db: AppDatabase) = db.plantDao()

    @Provides
    fun providePlantAlarmDao(db: AppDatabase) = db.plantAlarmDao()

    @Provides
    fun provideRegularScheduleDao(db: AppDatabase) = db.regularScheduleDao()

    @Provides
    fun provideWinterScheduleDao(db: AppDatabase) = db.winterScheduleDao()
}