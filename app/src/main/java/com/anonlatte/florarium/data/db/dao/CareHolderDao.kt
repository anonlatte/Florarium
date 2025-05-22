package com.anonlatte.florarium.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anonlatte.florarium.data.db.model.CareHolderEntity

@Dao
interface CareHolderDao : BaseDao<CareHolderEntity> {

    @Query("SELECT * FROM care_holder WHERE id = :id")
    suspend fun get(id: Long): CareHolderEntity?

    @Query("SELECT * FROM care_holder WHERE plantId = :plantId")
    suspend fun getByPlantId(plantId: Long): CareHolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMultiple(data: List<CareHolderEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareHolder(careHolder: CareHolderEntity): Long

    @Update
    suspend fun updateCareHolder(careHolder: CareHolderEntity): Int

    @Delete
    suspend fun deleteMultiple(data: List<CareHolderEntity>): Int

    @Query("DELETE FROM care_holder WHERE plantId = :plantId")
    suspend fun deleteCareHolderByPlantId(plantId: Long)
}