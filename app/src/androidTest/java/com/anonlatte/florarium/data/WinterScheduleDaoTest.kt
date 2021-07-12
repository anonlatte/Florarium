package com.anonlatte.florarium.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anonlatte.florarium.app.utils.testWinterSchedules
import com.anonlatte.florarium.data.db.AppDatabase
import com.anonlatte.florarium.data.db.dao.WinterScheduleDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WinterScheduleDaoTest {

    private lateinit var scheduleDao: WinterScheduleDao
    private lateinit var db: AppDatabase
    private var schedule = testWinterSchedules[0]

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        scheduleDao = db.winterScheduleDao()
        runBlocking {
            scheduleDao.createMultiple(testWinterSchedules)
            scheduleDao.create(testWinterSchedules[0])
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getSchedules() = runBlocking {
        val loaded = scheduleDao.getSchedules()
        MatcherAssert.assertThat(
            loaded.size,
            Matchers.equalTo(testWinterSchedules.size)
        )
    }

    @Test
    fun update() = runBlocking {
        schedule = schedule.copy(wateringInterval = 4)
        val updatedId = scheduleDao.update(schedule).toLong()
        MatcherAssert.assertThat(updatedId, Matchers.equalTo(schedule.scheduleId))
    }

    @Test
    fun delete() = runBlocking {
        val deletedCounter = scheduleDao.delete(schedule)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(1))
    }

    @Test
    fun deleteMultiple() = runBlocking {
        val deletedCounter = scheduleDao.deleteMultiple(testWinterSchedules)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(testWinterSchedules.size))
    }
}
