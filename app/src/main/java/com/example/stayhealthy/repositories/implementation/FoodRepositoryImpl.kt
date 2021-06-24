package com.example.stayhealthy.repositories.implementation

import com.example.stayhealthy.repositories.FoodRepository
import com.example.stayhealthy.utils.FirebaseStorageManager
import com.google.firebase.database.Query


class FoodRepositoryImpl(private val firebaseStorageManager: FirebaseStorageManager) : FoodRepository {

    override fun createFoodQuery(category: String): Query {
        return firebaseStorageManager.createFoodQuery(category)
    }

    override fun createFoodQuerySearchCondition(category: String, searchCondition: String): Query {
        return firebaseStorageManager.createFoodQuerySearchCondition(category, searchCondition)
    }
}