package com.example.stayhealthy.repository.implementation

import android.util.Log
import com.example.stayhealthy.extension.await
import com.example.stayhealthy.model.MealPlanItem
import com.example.stayhealthy.repository.MealPlanContract
import com.example.stayhealthy.repository.MealPlanRepository
import com.example.stayhealthy.repository.UsersContract
import com.example.stayhealthy.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


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

    override suspend fun getMealPlanQuery(userId: String, startDate: Long, endDate: Long): Result<ArrayList<MealPlanItem>?> {

        try
        {
            return when(val querySnapshot = firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(MealPlanContract.COLLECTION_NAME).whereGreaterThanOrEqualTo(MealPlanContract.Columns.MEAL_ITEM_DATE, startDate).whereLessThan(MealPlanContract.Columns.MEAL_ITEM_DATE, endDate).get().await())
            {
                is Result.Success -> {
                    val query = querySnapshot.data.documents
                    val meals = ArrayList<MealPlanItem>()
                    for (document in query) {
                        meals.add(document.toObject(MealPlanItem::class.java)!!)
                    }
                    Result.Success(meals)
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

    override suspend fun updateMealPlanItem(userId: String, mealPlanItem: MealPlanItem): Result<Void?> {
        return try
        {
            firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(MealPlanContract.COLLECTION_NAME).document(mealPlanItem.id).set(mealPlanItem).await()
        }
        catch (exception: java.lang.Exception)
        {
            Result.Error(exception)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun listenOnMealPlanChanged(userId: String, startDate: Long, endDate: Long): Flow<Result<ArrayList<MealPlanItem>?>> {
        Log.d(TAG, "listenOnMealPlanChanged starts")
        return callbackFlow { //  -> Kotlin Flows
            val listenerRegistration = firestoreInstance.collection(UsersContract.COLLECTION_NAME)
                    .document(userId).collection(MealPlanContract.COLLECTION_NAME)
                    .whereGreaterThanOrEqualTo(MealPlanContract.Columns.MEAL_ITEM_DATE, startDate)
                    .whereLessThan(MealPlanContract.Columns.MEAL_ITEM_DATE, endDate)
                    .addSnapshotListener { querySnapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            offer(Result.Error(e))
                        }
                        if (querySnapshot != null && !querySnapshot.isEmpty) {

                            val meals = ArrayList<MealPlanItem>()
                            for (document in querySnapshot) {
                                meals.add(document.toObject(MealPlanItem::class.java))
                            }
                            Log.d(TAG, "listenOnMealPlanChanged: listen success with $meals")
                            offer(Result.Success(meals)) // offer values to the channel that will be collected in ViewModel
                        } else {
                            Log.d(TAG, "Current data: null")
                        }
                    }
            //Finally if collect is not in use or collecting any data cancel this channel to prevent any leak and remove the subscription listener to the database
            awaitClose {
                Log.d(TAG, "Cancelling meal plan listener")
                listenerRegistration.remove()
            }
            Log.d(TAG, "listenOnMealPlanChanged ends")
        }
    }

}