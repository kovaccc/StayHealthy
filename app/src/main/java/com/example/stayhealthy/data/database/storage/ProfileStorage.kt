package com.example.stayhealthy.data.database.storage

import com.example.stayhealthy.data.database.dao.UserDao
import com.example.stayhealthy.data.models.domain.User

class ProfileStorage(private val userDao: UserDao) {

    fun getUserLiveData() = userDao.getUserLiveData()

    suspend fun getUserAsync() = userDao.getUserAsync().asDomain()

    val user: User
        get() = userDao.getUser().asDomain()

    fun getUserNullable() = userDao.getUserNullable()?.asDomain()

    suspend fun persistUser(user: User) {
        userDao.insert(user.asDatabase())
    }
}