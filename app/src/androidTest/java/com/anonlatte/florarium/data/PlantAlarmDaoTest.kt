package com.anonlatte.florarium.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anonlatte.florarium.app.utils.testPlantAlarm
import com.anonlatte.florarium.data.db.AppDatabase
import com.anonlatte.florarium.data.db.dao.PlantAlarmDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantAlarmDaoTest {

    private lateinit var scheduleDao: PlantAlarmDao
    private lateinit var db: AppDatabase
    private var schedule = testPlantAlarm[0]

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        scheduleDao = db.plantAlarmDao()
        runBlocking {
            scheduleDao.createMultiple(testPlantAlarm)
            scheduleDao.create(testPlantAlarm[0])
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getSchedules() = runBlocking {
        val loaded = scheduleDao.getPlantsAlarms()
        MatcherAssert.assertThat(loaded.size, Matchers.equalTo(testPlantAlarm.size))
    }

    @Test
    fun update() = runBlocking {
        schedule = schedule.copy(
            plantName = "aloe",
            eventTag = "new_event",
            interval = 5,
            lastCare = System.currentTimeMillis()
        )
        val updatedId = scheduleDao.update(schedule).toLong()
        MatcherAssert.assertThat(updatedId, Matchers.equalTo(schedule.requestId))
    }

    @Test
    fun delete() = runBlocking {
        val deletedCounter = scheduleDao.delete(schedule)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(1))
    }

    @Test
    fun deleteMultiple() = runBlocking {
        val deletedCounter = scheduleDao.deleteMultiple(testPlantAlarm)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(testPlantAlarm.size))
    }
}
