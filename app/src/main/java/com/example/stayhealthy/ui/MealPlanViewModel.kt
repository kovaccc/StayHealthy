package com.example.stayhealthy.ui

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayhealthy.R
import com.example.stayhealthy.model.MealPlanItem
import com.example.stayhealthy.repository.MealPlanRepository
import com.example.stayhealthy.utils.Result
import kotlinx.coroutines.launch


private const val TAG = "MealPlanViewModel"

class MealPlanViewModel(var mealPlanRepository: MealPlanRepository) : ViewModel() {

    private val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast


     fun createMealPlanItemInFirestore(userId: String, mealPlanItem: MealPlanItem, activity:Activity)
    {
        Log.d(TAG, "createMealPlanItemInFirestore starts with  - ${userId}, mealPlanItem $mealPlanItem")

        viewModelScope.launch {
            when (val result = mealPlanRepository.addMealPlanItem(userId, mealPlanItem)) {
                is Result.Success -> {
                    Log.d(TAG, "createMealPlanItemInFirestore is Result.Success - $mealPlanItem")

                }
                is Result.Error -> {
                    _toast.value = result.exception.message

                }
                is Result.Canceled -> {
                    _toast.value = activity.getString(R.string.request_canceled)

                }
            }
        }
        Log.d(TAG, "createMealPlanItemInFirestore ends: Result - $mealPlanItem")
    }

    fun onToastShown()
    {
        _toast.value = null
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared starts")
        super.onCleared()
        Log.d(TAG, "onCleared ends")
    }

}