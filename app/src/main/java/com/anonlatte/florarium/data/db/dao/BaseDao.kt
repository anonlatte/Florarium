package com.anonlatte.florarium.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(data: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createMultiple(data: List<T>): List<Long>

    @Update
    fun update(data: T): Int

    @Delete
    fun delete(data: T): Int

    @Delete
    fun deleteMultiple(data: List<T>): Int
}
