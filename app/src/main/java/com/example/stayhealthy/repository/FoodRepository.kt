package com.example.stayhealthy.repository

import com.google.firebase.database.Query

interface FoodRepository {

    fun createFoodQuery(category: String) : Query

    fun createFoodQuerySearchCondition(category: String, searchCondition: String) : Query

}