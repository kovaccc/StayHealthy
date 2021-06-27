package com.example.stayhealthy.repositories

import android.net.Uri
import com.example.stayhealthy.data.models.domain.UserFood
import com.example.stayhealthy.util.Result
import com.google.firebase.database.Query
import java.io.File

interface FoodRepository {

    fun createFoodQuery(category: String): Query

//    fun createUserFoodQuery(userId: String, category: String): com.google.firebase.firestore.Query

    fun createUserFoodQuery(userId: String, category: String): Query

    fun createFoodQuerySearchCondition(category: String, searchCondition: String): Query

    fun createUserFoodQuerySearchCondition(userId: String, category: String, searchCondition: String): Query

    fun createFoodImageFile(): File?

    suspend fun addUserFood(userId: String, food: UserFood, foodUri: Uri): Result<Void?>?
}