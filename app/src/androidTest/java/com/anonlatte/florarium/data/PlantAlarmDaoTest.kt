package com.anonlatte.florarium.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anonlatte.florarium.app.utils.testPlantAlarm
import com.anonlatte.florarium.data.db.AppDatabase
import com.anonlatte.florarium.data.db.dao.PlantAlarmDao
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantAlarmDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scheduleDao: PlantAlarmDao
    private lateinit var db: AppDatabase
    private val schedule = testPlantAlarm[0]

    @Before
    @Throws(Exception::class)
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        scheduleDao = db.plantAlarmDao()
        scheduleDao.createMultiple(testPlantAlarm)
        scheduleDao.create(testPlantAlarm[0])
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun getSchedules() {
        val loaded = scheduleDao.getPlantsAlarms()
        MatcherAssert.assertThat(loaded.size, Matchers.equalTo(testPlantAlarm.size))
    }

    @Test
    fun update() {
        schedule.plantName = "aloe"
        schedule.eventTag = "new_event"
        schedule.interval = 5
        schedule.lastCare = System.currentTimeMillis()
        val updatedId = scheduleDao.update(schedule).toLong()
        MatcherAssert.assertThat(updatedId, Matchers.equalTo(schedule.requestId))
    }

    @Test
    fun delete() {
        val deletedCounter = scheduleDao.delete(schedule)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(1))
    }

    @Test
    fun deleteMultiple() {
        val deletedCounter = scheduleDao.deleteMultiple(testPlantAlarm)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(testPlantAlarm.size))
    }
}
