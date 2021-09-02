package com.example.stayhealthy.data.models.domain

import com.example.stayhealthy.common.base.DomainData
import com.example.stayhealthy.data.models.persistance.DBUser


data class User(
        var id: String = "",
        var name: String = "",
        var gender: String = "",
        var age: Long = 0,
        var height: Long = 0,
        var weight: Long = 0,
        var activityLevel: String = ""
) : DomainData<DBUser> {


    override fun toString(): String {
        return "${this.id}, ${this.name}, ${this.gender}, ${this.age}, ${this.height}, ${this.weight}, ${this.activityLevel}"
    }

    override fun asDatabase(): DBUser {
        return DBUser(
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



