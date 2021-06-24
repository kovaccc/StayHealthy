package com.example.stayhealthy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.example.stayhealthy.repositories.MealPlanRepository
import com.example.stayhealthy.utils.Result
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val TAG = "MealPlanViewModel"

class MealPlanViewModel(var mealPlanRepository: MealPlanRepository) : ViewModel() {

    private val _toastMLD = MutableLiveData<String?>()
    val toastLD: LiveData<String?>
        get() = _toastMLD


    fun createMealPlanItemInFirestore(userId: String, mealPlanItem: MealPlanItem) {
        Log.d(TAG, "createMealPlanItemInFirestore starts with  - ${userId}, mealPlanItem $mealPlanItem")

        viewModelScope.launch {
            when (val result = withContext(IO) { mealPlanRepository.addMealPlanItem(userId, mealPlanItem) }) { //switch to IO dispatcher for network call
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

    fun onToastShown() {
        _toastMLD.value = null
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared starts")
        super.onCleared()
        Log.d(TAG, "onCleared ends")
    }

}