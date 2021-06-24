package com.example.stayhealthy.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.stayhealthy.common.base.BaseDao
import com.example.stayhealthy.config.TABLE_USER
import com.example.stayhealthy.data.models.persistance.DBUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao: BaseDao<DBUser> {

    @Query("Select * from $TABLE_USER limit 1")
    fun isLoggedIn(): DBUser?

    @Query("Select * from $TABLE_USER limit 1")
    fun getUser(): DBUser

    @Query("Select * from $TABLE_USER limit 1")
    fun getUserNullable(): DBUser?

    @Query("Select * from $TABLE_USER limit 1")
    fun getUserLiveData(): Flow<DBUser?>

    @Query("Select * from $TABLE_USER limit 1")
    suspend fun getUserAsync(): DBUser

}