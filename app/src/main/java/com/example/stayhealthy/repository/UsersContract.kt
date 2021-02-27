package com.example.stayhealthy.repository


import android.provider.BaseColumns

object UsersContract {


    internal const val COLLECTION_NAME = "users"
    // Users fields
    object Columns {
        const val ID = BaseColumns._ID
        const val USER_ID = "id"
        const val USER_NAME = "name"
        const val USER_GENDER = "gender"
        const val USER_AGE = "age"
        const val USER_HEIGHT = "height"
        const val USER_WEIGHT = "weight"
        const val USER_ACTIVITY_LEVEL = "activityLevel"
    }

}