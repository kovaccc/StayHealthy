package com.example.stayhealthy.repository.implementation

import com.example.stayhealthy.extension.await
import com.example.stayhealthy.model.MealPlanItem
import com.example.stayhealthy.repository.MealPlanContract
import com.example.stayhealthy.repository.MealPlanRepository
import com.example.stayhealthy.repository.UsersContract
import com.example.stayhealthy.utils.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


private const val TAG = "MealPlanRepositoryIm"
class MealPlanRepositoryImpl : MealPlanRepository {

    private val firestoreInstance = FirebaseFirestore.getInstance()

    override fun createMealPlanQuery(userId: String, startDate: Long, endDate: Long) : Query { // firestore adapter is listening to firestore query so just return configuration

        return firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(MealPlanContract.COLLECTION_NAME).whereGreaterThanOrEqualTo(MealPlanContract.Columns.MEAL_ITEM_DATE, startDate).whereLessThan(MealPlanContract.Columns.MEAL_ITEM_DATE, endDate)

    }



    override suspend fun addMealPlanItem(userId: String, mealPlanItem: MealPlanItem): Result<Void?> {
        return try
        {
            val documentRef = firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(MealPlanContract.COLLECTION_NAME).document()
            mealPlanItem.id = documentRef.id
            firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(MealPlanContract.COLLECTION_NAME).document(mealPlanItem.id).set(mealPlanItem).await()
        }
        catch (exception: java.lang.Exception)
        {
            Result.Error(exception)
        }
    }

    override suspend fun getMealPlanQuery(userId: String, startDate: Long, endDate: Long): Result<List<DocumentSnapshot>> {

        try
        {
            return when(val querySnapshot = firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(MealPlanContract.COLLECTION_NAME).whereGreaterThanOrEqualTo(MealPlanContract.Columns.MEAL_ITEM_DATE, startDate).whereLessThan(MealPlanContract.Columns.MEAL_ITEM_DATE, endDate).get().await())
            {
                is Result.Success -> {
                    val query = querySnapshot.data.documents
                    Result.Success(query)
                }
                is Result.Error -> Result.Error(querySnapshot.exception)
                is Result.Canceled -> Result.Canceled(querySnapshot.exception)
            }
        }
        catch (exception: Exception)
        {
            return Result.Error(exception)
        }
    }

}