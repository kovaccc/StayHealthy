package com.example.stayhealthy.repositories.implementation

import android.net.Uri
import com.example.stayhealthy.data.models.domain.UserFood
import com.example.stayhealthy.repositories.FoodRepository
import com.example.stayhealthy.util.FirebaseStorageManager
import com.example.stayhealthy.util.ImageFileHelper
import com.example.stayhealthy.util.Result
import com.google.firebase.database.Query
import java.io.File


class FoodRepositoryImpl(
        private val firebaseStorageManager: FirebaseStorageManager,
        private val imageFileHelper: ImageFileHelper
) : FoodRepository {

    override fun createFoodQuery(category: String): Query {
        return firebaseStorageManager.createFoodQuery(category)
    }

    override fun createUserFoodQuery(userId: String, category: String): Query {
        return firebaseStorageManager.createUserFoodQuery(userId, category)
    }

    override fun createFoodQuerySearchCondition(category: String, searchCondition: String): Query {
        return firebaseStorageManager.createFoodQuerySearchCondition(category, searchCondition)
    }

    override fun createUserFoodQuerySearchCondition(
            userId: String,
            category: String,
            searchCondition: String
    ): Query {
        return firebaseStorageManager.createUserFoodQuerySearchCondition(
                userId,
                category,
                searchCondition
        )
    }

    override fun createFoodImageFile(): File? {
        return imageFileHelper.createImageFile()
    }

    override suspend fun addUserFood(userId: String, food: UserFood, foodUri: Uri): Result<Void?>? {
        return when (val uploadResult = firebaseStorageManager.uploadFoodImage(userId, foodUri)) {
            is Result.Success -> {
                uploadResult.data?.let {
                    food.image = it.toString()
                    firebaseStorageManager.addUserFood(userId, food)
                }
            }
            is Result.Error -> {
                Result.Error(uploadResult.exception)
            }
            is Result.Canceled -> {
                Result.Canceled(uploadResult.exception)
            }
        }
    }


}