package com.example.stayhealthy.common.base

import androidx.room.*
import com.example.stayhealthy.config.TABLE_USER

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(models: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMultiple(models: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(model: T)

    @Delete
    suspend fun deleteMultiple(models: List<T>)

    @Delete
    suspend fun delete(model: T)

    @Query("Select id from $TABLE_USER limit 1")
    fun userId(): Int

}