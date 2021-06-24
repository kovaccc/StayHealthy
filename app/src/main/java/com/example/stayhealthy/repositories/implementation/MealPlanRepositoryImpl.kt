package com.example.stayhealthy.repositories.implementation

import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.example.stayhealthy.repositories.MealPlanRepository
import com.example.stayhealthy.utils.FirebaseStorageManager
import com.example.stayhealthy.utils.Result
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

private const val TAG = "MealPlanRepositoryIm"

class MealPlanRepositoryImpl(private val firebaseStorageManager: FirebaseStorageManager) :
        MealPlanRepository {

    override fun createMealPlanQuery(
            userId: String,
            startDate: Long,
            endDate: Long
    ): Query {
        return firebaseStorageManager.createMealPlanQuery(userId, startDate, endDate)
    }


    override suspend fun addMealPlanItem(
            userId: String,
            mealPlanItem: MealPlanItem
    ): Result<Void?> {
        return firebaseStorageManager.addMealPlanItem(userId, mealPlanItem)
    }

    override suspend fun getMealPlanQuery(
            userId: String,
            startDate: Long,
            endDate: Long
    ): Result<ArrayList<MealPlanItem>?> {
        return firebaseStorageManager.getMealPlanQuery(userId, startDate, endDate)
    }

    override suspend fun updateMealPlanItem(
            userId: String,
            mealPlanItem: MealPlanItem
    ): Result<Void?> {
        return firebaseStorageManager.updateMealPlanItem(userId, mealPlanItem)
    }

    @ExperimentalCoroutinesApi
    override suspend fun listenOnMealPlanChanged(
            userId: String,
            startDate: Long,
            endDate: Long
    ): Flow<Result<ArrayList<MealPlanItem>?>> {
        return firebaseStorageManager.listenOnMealPlanChanged(userId, startDate, endDate)
    }

}