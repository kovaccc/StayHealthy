package com.example.stayhealthy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayhealthy.common.CalculationMethods
import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.example.stayhealthy.repositories.MealPlanRepository
import com.example.stayhealthy.repositories.UserRepository
import com.example.stayhealthy.util.Result
import com.example.stayhealthy.util.TimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


private const val TAG = "MealPlanViewModel"

class MealPlanViewModel(
        private val mealPlanRepository: MealPlanRepository,
        private val userRepository: UserRepository
) : ViewModel() {

    private val _toastMLD = MutableLiveData<String?>()
    val toastLD: LiveData<String?>
        get() = _toastMLD

    fun createMealPlanItemInFirestore(userId: String, mealPlanItem: MealPlanItem) {
        Log.d(
                TAG,
                "createMealPlanItemInFirestore starts with  - ${userId}, mealPlanItem $mealPlanItem"
        )

        viewModelScope.launch {
            when (val result = withContext(IO) {
                mealPlanRepository.addMealPlanItem(
                        userId,
                        mealPlanItem
                )
            }) { //switch to IO dispatcher for network call
                is Result.Success -> {
                    Log.d(TAG, "createMealPlanItemInFirestore is Result.Success - $mealPlanItem")
                }
                is Result.Error -> {
                    _toastMLD.value = result.exception.message

                }
                is Result.Canceled -> {
                    _toastMLD.value = result.exception?.message
                }
            }
        }
        Log.d(TAG, "createMealPlanItemInFirestore ends: Result - $mealPlanItem")
    }

    suspend fun getCurrentCategoryCalorieNeeds(
            date: Long,
            category: String
    ): Int {
        val currentUser = userRepository.getLocalUserAsync()
        val meals = getMealPlanFromFirestore(currentUser.id, date)
        var categoryCalories = 0
        withContext(Dispatchers.Default) {
            categoryCalories =
                    meals.filter { it.category == category }
                            .map { it.calories.toInt() }
                            .sum()
        }
        val calculationMethods = CalculationMethods(currentUser)
        return calculationMethods.calculateFoodCalorieNeeds(category) - categoryCalories
    }


    private suspend fun getMealPlanFromFirestore(
            userId: String,
            date: Long
    ): ArrayList<MealPlanItem> {

        Log.d(TAG, "getMealPlanFromFirestore: starts with $userId AND $date")

        var meals = ArrayList<MealPlanItem>()

        val startTime = TimeHelper.getStartTimeOfDate(date)
        val endTime = TimeHelper.getEndTimeOfDate(date)

        when (val result = withContext(IO) {
            mealPlanRepository.getMealPlanQuery(
                    userId,
                    startTime,
                    endTime
            )
        }) { //background thread for heavy network operation
            is Result.Success -> {
                result.data?.let { meals = it }

                Log.d(
                        TAG,
                        "getMealPlanFromFirestore is Result.Success, meal plan is ${result.data}"
                )

            }
            is Result.Error -> {
                Log.e(TAG, "${result.exception.message}")
                _toastMLD.value = result.exception.message
            }
            is Result.Canceled -> {
                Log.e(TAG, "${result.exception!!.message}")
                _toastMLD.value = "Request canceled"
            }
        }

        Log.d(TAG, "getMealPlanFromFirestore: ends, meal plan is $meals")
        return meals
    }


    fun onToastShown() {
        _toastMLD.value = null
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared starts")
        super.onCleared()
        Log.d(TAG, "onCleared ends")
    }

}