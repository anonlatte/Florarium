package com.anonlatte.florarium.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.anonlatte.florarium.app.utils.testPlants
import com.anonlatte.florarium.data.db.AppDatabase
import com.anonlatte.florarium.data.db.dao.PlantDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlantDaoTest {

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
        runBlocking {
            plantDao.createMultiple(testPlants)
            plantDao.create(plant)
        }
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testGetPlants() = runBlocking {
        val loaded = plantDao.getPlants()
        assertThat(loaded.size, equalTo(testPlants.size))
    }

    @Test
    fun testUpdatePlant() = runBlocking {
        plant.name = "chamomile"
        val updatedId = plantDao.update(plant).toLong()
        assertThat(updatedId, equalTo(plant.id))
    }

    @Test
    fun testDeletePlant() = runBlocking {
        val deletedCounter = plantDao.delete(plant)
        assertThat(deletedCounter, equalTo(1))
    }

    @Test
    fun testDeletePlants() = runBlocking {
        val deletedCounter = plantDao.deleteMultiple(testPlants)
        assertThat(deletedCounter, equalTo(testPlants.size))
    }
}
