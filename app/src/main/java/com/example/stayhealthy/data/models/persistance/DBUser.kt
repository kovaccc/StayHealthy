package com.example.stayhealthy.data.models.persistance

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stayhealthy.common.base.Persistable
import com.example.stayhealthy.config.TABLE_USER
import com.example.stayhealthy.data.models.domain.User


@Entity(tableName = TABLE_USER)
data class DBUser(
        @PrimaryKey val id: String,
        val name: String,
        val gender: String,
        val age: Long,
        val height: Long,
        val weight: Long,
        val activityLevel: String
) : Persistable<User> {
    override fun asDomain(): User {
        return User(
                id,
                name,
                gender,
                age,
                height,
                weight,
                activityLevel
        )
    }
}
