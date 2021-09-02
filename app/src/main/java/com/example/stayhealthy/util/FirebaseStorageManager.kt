package com.example.stayhealthy.util

import android.net.Uri
import android.util.Log
import com.example.stayhealthy.common.contracts.KnowledgeBaseContract
import com.example.stayhealthy.common.contracts.MealPlanContract
import com.example.stayhealthy.common.contracts.MenuContract
import com.example.stayhealthy.common.contracts.UsersContract
import com.example.stayhealthy.common.extensions.await
import com.example.stayhealthy.config.FIREBASE_STORAGE_BASE_URL
import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.example.stayhealthy.data.models.domain.User
import com.example.stayhealthy.data.models.domain.UserFood
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


private const val TAG = "FirebaseStorageManager"

class FirebaseStorageManager {

    private val firebaseInstance = FirebaseDatabase.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val dbStorage = Firebase.storage

    fun createFoodQuery(category: String): Query {
        return firebaseInstance.getReference(MenuContract.ROOT_NAME).child(category)
    }

    fun createFoodQuerySearchCondition(category: String, searchCondition: String): Query {
        return firebaseInstance.getReference(MenuContract.ROOT_NAME).child(category).orderByChild(
                MenuContract.Columns.MENU_ITEM_NAME
        ).startAt(searchCondition).endAt(searchCondition + "\uf8ff")
    }

    fun createUserFoodQuerySearchCondition(userId: String, category: String, searchCondition: String): Query {

        return firebaseInstance.getReference(MenuContract.ROOT_NAME)
                .child(MenuContract.USERS_FOOD_CHILD).child(userId).child(category)
                .orderByChild(
                        MenuContract.Columns.MENU_ITEM_NAME
                ).startAt(searchCondition).endAt(searchCondition + "\uf8ff")
    }



    fun createMealPlanQuery(
            userId: String,
            startDate: Long,
            endDate: Long
    ): com.google.firebase.firestore.Query { // firestore adapter is listening to firestore query so just return configuration

        return firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId)
                .collection(
                        MealPlanContract.COLLECTION_NAME
                ).whereGreaterThanOrEqualTo(MealPlanContract.Columns.MEAL_ITEM_DATE, startDate)
                .whereLessThan(
                        MealPlanContract.Columns.MEAL_ITEM_DATE, endDate
                )

    }

    suspend fun addMealPlanItem(userId: String, mealPlanItem: MealPlanItem): Result<Void?> {
        return try {
            val documentRef =
                    firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId)
                            .collection(
                                    MealPlanContract.COLLECTION_NAME
                            ).document()
            mealPlanItem.id = documentRef.id
            firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(
                    MealPlanContract.COLLECTION_NAME
            ).document(mealPlanItem.id).set(mealPlanItem).await()
        } catch (exception: java.lang.Exception) {
            Result.Error(exception)
        }
    }


    suspend fun getMealPlanQuery(
            userId: String,
            startDate: Long,
            endDate: Long
    ): Result<ArrayList<MealPlanItem>?> {

        try {
            return when (val querySnapshot =
                    firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId)
                            .collection(
                                    MealPlanContract.COLLECTION_NAME
                            ).whereGreaterThanOrEqualTo(MealPlanContract.Columns.MEAL_ITEM_DATE, startDate)
                            .whereLessThan(
                                    MealPlanContract.Columns.MEAL_ITEM_DATE, endDate
                            ).get().await()) {
                is Result.Success -> {
                    val query = querySnapshot.data.documents
                    val meals = ArrayList<MealPlanItem>()
                    for (document in query) {
                        document.toObject(MealPlanItem::class.java)?.let { meals.add(it) }
                    }
                    Result.Success(meals)
                }
                is Result.Error -> Result.Error(querySnapshot.exception)
                is Result.Canceled -> Result.Canceled(querySnapshot.exception)
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }

    suspend fun updateMealPlanItem(userId: String, mealPlanItem: MealPlanItem): Result<Void?> {
        return try {
            firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).collection(
                    MealPlanContract.COLLECTION_NAME
            ).document(mealPlanItem.id).set(mealPlanItem).await()
        } catch (exception: java.lang.Exception) {
            Result.Error(exception)
        }
    }


    @ExperimentalCoroutinesApi
    suspend fun listenOnMealPlanChanged(
            userId: String,
            startDate: Long,
            endDate: Long
    ): Flow<Result<ArrayList<MealPlanItem>?>> {
        Log.d(TAG, "listenOnMealPlanChanged starts")
        return callbackFlow {
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


    suspend fun createUserInFirestore(user: User): Result<Void?> {
        return try {
            firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(user.id).set(user)
                    .await()
        } catch (exception: java.lang.Exception) {
            Result.Error(exception)
        }
    }

    suspend fun getUserFromFirestore(userId: String): Result<User> {
        return try {
            when (val resultDocumentSnapshot =
                    firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(userId).get()
                            .await()) {
                is Result.Success -> {
                    val user = resultDocumentSnapshot.data.toObject(User::class.java)!!
                    Result.Success(user)
                }
                is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
                is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
            }
        } catch (exception: java.lang.Exception) {
            Result.Error(exception)
        }
    }


    suspend fun updateUserInFirestore(user: User): Result<Void?> {
        return try {
            firestoreInstance.collection(UsersContract.COLLECTION_NAME).document(user.id).set(user)
                    .await()
        } catch (exception: java.lang.Exception) {
            Result.Error(exception)
        }
    }


    @ExperimentalCoroutinesApi
    suspend fun listenOnUserChanged(userId: String): Flow<Result<User>?> {

        Log.d(TAG, "listenOnUserChanged starts")
        return callbackFlow { //  -> Kotlin Flows
            val listenerRegistration = firestoreInstance.collection(UsersContract.COLLECTION_NAME)
                    .document(userId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            offer(Result.Error(e))
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val user = snapshot.toObject(User::class.java)!!
                            Log.d(TAG, "listenOnUserChanged: listen success with $user")
                            offer(Result.Success(user)) // offer values to the channel that will be collected in ViewModel
                        } else {
                            Log.d(TAG, "Current data: null")
                        }
                    }
            //Finally if collect is not in use or collecting any data cancel this channel to prevent any leak and remove the subscription listener to the database
            awaitClose {
                Log.d(TAG, "Cancelling user listener")
                listenerRegistration.remove()
            }
            Log.d(TAG, "listenOnUserChanged ends")
        }
    }

    suspend fun uploadFoodImage(userId: String, image: Uri): Result<Uri?> {
        val storageRef = dbStorage.reference
        val imageRef = storageRef.child(FIREBASE_STORAGE_BASE_URL)
        val userImageRef = imageRef.child(userId).child(image.lastPathSegment!!) // we want user id in path

        return try {
            when (val uploadResult = userImageRef.putFile(image).await()) {
                is Result.Success -> {
                    uploadResult.data.storage.downloadUrl.await()
                }
                is Result.Error -> Result.Error(uploadResult.exception)
                is Result.Canceled -> Result.Canceled(uploadResult.exception)
            }
        } catch (exception: java.lang.Exception) {
            Result.Error(exception)
        }

    }

    suspend fun addUserFood(userId: String, userFood: UserFood): Result<Void?> {

        return try {

            val food = userFood.asFood()
            val foodToSave = HashMap<String, Any>()
            foodToSave.apply {
                put(MenuContract.Columns.MENU_ITEM_NAME, food.Name)
                put(MenuContract.Columns.MENU_ITEM_CALORIES, food.Calories)
                put(MenuContract.Columns.MENU_ITEM_QUANTITY, food.Quantity)
                put(MenuContract.Columns.MENU_ITEM_IMAGE, food.Image)
            }
            val usersFoodRef = firebaseInstance.getReference(MenuContract.ROOT_NAME)
                    .child(MenuContract.USERS_FOOD_CHILD).child(userId).child(userFood.category)
                    .push()

            usersFoodRef.setValue(foodToSave).await()

        } catch (exception: java.lang.Exception) {
            Result.Error(exception)
        }
    }

    fun createUserFoodQuery(
            userId: String,
            category: String
    ): Query {

        return firebaseInstance.getReference(MenuContract.ROOT_NAME)
                .child(MenuContract.USERS_FOOD_CHILD).child(userId).child(category)

    }


    fun createKnowledgeBaseQuery(): Query {
        return firebaseInstance.getReference(KnowledgeBaseContract.ROOT_NAME)
    }

}