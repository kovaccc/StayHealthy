package com.example.stayhealthy.repositories

import com.google.firebase.database.Query

interface FoodRepository {

    fun createFoodQuery(category: String): Query

    fun createFoodQuerySearchCondition(category: String, searchCondition: String): Query

}