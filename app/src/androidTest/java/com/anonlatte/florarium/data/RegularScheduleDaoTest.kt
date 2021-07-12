package com.anonlatte.florarium.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anonlatte.florarium.app.utils.testRegularSchedules
import com.anonlatte.florarium.data.db.AppDatabase
import com.anonlatte.florarium.data.db.dao.RegularScheduleDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegularScheduleDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var scheduleDao: RegularScheduleDao
    private lateinit var db: AppDatabase
    private var schedule = testRegularSchedules[0]

    @Before
    fun setUp() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        scheduleDao = db.regularScheduleDao()
        scheduleDao.createMultiple(testRegularSchedules)
        scheduleDao.create(testRegularSchedules[0])
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
            Matchers.equalTo(testRegularSchedules.size)
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
        val deletedCounter = scheduleDao.deleteMultiple(testRegularSchedules)
        MatcherAssert.assertThat(deletedCounter, Matchers.equalTo(testRegularSchedules.size))
    }
}
