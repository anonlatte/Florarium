package com.anonlatte.florarium.di.module

import android.content.Context
import androidx.room.Room
import com.anonlatte.florarium.app.utils.DATABASE_NAME
import com.anonlatte.florarium.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context, AppDatabase::class.java, DATABASE_NAME
    ).build()

    @Provides
    fun providePlantDao(db: AppDatabase) = db.plantDao()

    @Provides
    fun provideRegularScheduleDao(db: AppDatabase) = db.regularScheduleDao()

    @Provides
    fun provideCareHolderDao(db: AppDatabase) = db.careHolderDao()

    @Provides
    fun provideCareArrangerDao(db: AppDatabase) = db.careArrangerDao()
}