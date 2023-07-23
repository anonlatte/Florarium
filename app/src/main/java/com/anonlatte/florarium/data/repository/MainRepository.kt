package com.anonlatte.florarium.data.repository

import com.anonlatte.florarium.data.db.dao.CareArrangerDao
import com.anonlatte.florarium.data.db.dao.CareHolderDao
import com.anonlatte.florarium.data.db.dao.PlantDao
import com.anonlatte.florarium.data.db.dao.RegularScheduleDao
import com.anonlatte.florarium.data.db.model.CareArrangerEntity
import com.anonlatte.florarium.data.db.model.CareHolderEntity.Companion.toDomain
import com.anonlatte.florarium.data.db.model.PlantEntity.Companion.toDomain
import com.anonlatte.florarium.data.db.model.RegularScheduleEntity.Companion.toDomain
import com.anonlatte.florarium.data.domain.CareHolder
import com.anonlatte.florarium.data.domain.CareHolder.Companion.toEntity
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.Plant.Companion.toEntity
import com.anonlatte.florarium.data.domain.PlantWithSchedule
import com.anonlatte.florarium.data.domain.RegularSchedule
import com.anonlatte.florarium.data.domain.RegularSchedule.Companion.toEntity
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val careArrangerDao: CareArrangerDao,
    private val careHolderDao: CareHolderDao,
    private val plantDao: PlantDao,
    private val regularScheduleDao: RegularScheduleDao,
) : IMainRepository {

    override suspend fun createPlant(
        plant: Plant,
        regularSchedule: RegularSchedule,
        careHolder: CareHolder,
    ) {
        val plantId = plantDao.create(plant.toEntity())
        val regularScheduleId = regularScheduleDao.create(regularSchedule.toEntity())
        val careHolderId = careHolderDao.create(careHolder.toEntity())
        careArrangerDao.create(CareArrangerEntity(plantId, regularScheduleId, careHolderId))
    }

    override suspend fun getPlants(): List<Plant> = plantDao.getPlants().map { it.toDomain() }
    override suspend fun updatePlant(plant: Plant): Int = plantDao.update(plant.toEntity())
    override suspend fun deletePlants(plants: List<Plant>): Int =
        careArrangerDao.deleteByPlantsIds(plants.map { it.id })

    override suspend fun updateSchedule(
        regularSchedule: RegularSchedule?,
    ) {
        if (regularSchedule?.id != null) {
            regularScheduleDao.update(regularSchedule.toEntity())
        }
    }

    override suspend fun getRegularScheduleList(): List<RegularSchedule> {
        return regularScheduleDao.getSchedules().map { it.toDomain() }
    }

    override suspend fun getPlantsToSchedules(): List<PlantWithSchedule> {
        val embeddedCareArranger: List<PlantWithSchedule> =
            careArrangerDao.getEmbeddedCareArranger().sortedByDescending {
                it.plant.createdAt
            }.map {
                PlantWithSchedule(
                    it.plant.toDomain(),
                    it.regularSchedule.toDomain(),
                    it.careHolder.toDomain()
                )
            }
        return embeddedCareArranger
    }

    override suspend fun updateGlobalNotificationTime(hour: Int, minute: Int) {
        val schedules = regularScheduleDao.getSchedules()
        schedules.forEach { schedule ->
            // TODO regularScheduleDao.update(schedule.copy(hour = hour, minute = minute))
        }
    }
}
