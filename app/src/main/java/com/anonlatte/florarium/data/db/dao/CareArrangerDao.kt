package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.anonlatte.florarium.data.db.model.CareArrangerEntity
import com.anonlatte.florarium.data.db.model.EmbeddedCareArranger

@Dao
interface CareArrangerDao : BaseDao<CareArrangerEntity> {

    @Query("SELECT * FROM care_arranger WHERE plantId = :plantId")
    suspend fun get(plantId: Long): CareArrangerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMultiple(data: List<CareArrangerEntity>): List<Long>

    @Delete
    suspend fun deleteMultiple(data: List<CareArrangerEntity>): Int

    @Query("DELETE FROM care_arranger WHERE plantId IN (:plantsIds)")
    suspend fun deleteByPlantsIds(plantsIds: List<Long>): Int

    @Transaction
    @Query("SELECT * FROM care_arranger")
    suspend fun getEmbeddedCareArranger(): List<EmbeddedCareArranger>
}