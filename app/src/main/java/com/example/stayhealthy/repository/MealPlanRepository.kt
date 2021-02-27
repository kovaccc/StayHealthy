package com.example.stayhealthy.repository


import com.example.stayhealthy.model.MealPlanItem
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.example.stayhealthy.utils.Result

interface MealPlanRepository {

    fun createMealPlanQuery(userId: String, startDate: Long, endDate: Long) : Query

    suspend fun addMealPlanItem(userId: String, mealPlanItem: MealPlanItem): Result<Void?>

    suspend fun getMealPlanQuery(userId: String, startDate: Long, endDate: Long) : Result<List<DocumentSnapshot>>

}