package com.anonlatte.florarium.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anonlatte.florarium.app.utils.getOrAwaitValue
import com.anonlatte.florarium.app.utils.testWinterSchedules
import com.anonlatte.florarium.data.db.AppDatabase
import com.anonlatte.florarium.data.db.dao.WinterScheduleDao
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WinterScheduleDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scheduleDao: WinterScheduleDao
    private lateinit var db: AppDatabase
    private val schedule = testWinterSchedules[0]

    @Before
    @Throws(Exception::class)
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        scheduleDao = db.winterScheduleDao()
        scheduleDao.createMultiple(testWinterSchedules)
        scheduleDao.create(testWinterSchedules[0])
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun getSchedules() {
        val loaded = scheduleDao.getSchedules()
        MatcherAssert.assertThat(
            loaded.getOrAwaitValue().size,
            Matchers.equalTo(testWinterSchedules.size)
        )
    }

    @Test
    fun update() {
        schedule.wateringInterval = 4
        val updatedId = scheduleDao.update(schedule).toLong()
        MatcherAssert.assertThat(updatedId, Matchers.equalTo(schedule.scheduleId))
    }

    @Test
    fun delete() {
        val deletedCounter = scheduleDao.delete(schedule)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(1))
    }

    @Test
    fun deleteMultiple() {
        val deletedCounter = scheduleDao.deleteMultiple(testWinterSchedules)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(testWinterSchedules.size))
    }
}
