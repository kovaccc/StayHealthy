package com.example.stayhealthy.repositories


import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.google.firebase.firestore.Query
import com.example.stayhealthy.utils.Result
import kotlinx.coroutines.flow.Flow

interface MealPlanRepository {

    fun createMealPlanQuery(userId: String, startDate: Long, endDate: Long): Query

    suspend fun addMealPlanItem(userId: String, mealPlanItem: MealPlanItem): Result<Void?>

    suspend fun getMealPlanQuery(userId: String, startDate: Long, endDate: Long): Result<ArrayList<MealPlanItem>?>

    suspend fun updateMealPlanItem(userId: String, mealPlanItem: MealPlanItem): Result<Void?>

    suspend fun listenOnMealPlanChanged(userId: String, startDate: Long, endDate: Long): Flow<Result<ArrayList<MealPlanItem>?>>

}