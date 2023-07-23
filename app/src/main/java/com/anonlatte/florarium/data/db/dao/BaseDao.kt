package com.anonlatte.florarium.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(data: T): Long

    @Update
    suspend fun update(data: T): Int

    @Delete
    suspend fun delete(data: T): Int
}
