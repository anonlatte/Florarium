package com.anonlatte.florarium.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anonlatte.florarium.db.AppDatabase
import com.anonlatte.florarium.db.dao.PlantDao
import com.anonlatte.florarium.utilities.getOrAwaitValue
import com.anonlatte.florarium.utilities.testPlants
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var plantDao: PlantDao
    private lateinit var db: AppDatabase
    private val plant = testPlants[0]

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        plantDao = db.plantDao()
        plantDao.createMultiple(testPlants)
        plantDao.create(plant)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testGetPlants() {
        val loaded = plantDao.getPlants()
        assertThat(loaded.getOrAwaitValue().size, equalTo(testPlants.size))
    }

    @Test
    fun testUpdatePlant() {
        plant.name = "chamomile"
        val updatedId = plantDao.update(plant).toLong()
        assertThat(updatedId, equalTo(plant.plantId))
    }

    @Test
    fun testDeletePlant() {
        val deletedCounter = plantDao.delete(plant)
        assertThat(deletedCounter, equalTo(1))
    }

    @Test
    fun testDeletePlants() {
        val deletedCounter = plantDao.deleteMultiple(testPlants)
        assertThat(deletedCounter, equalTo(testPlants.size))
    }
}
