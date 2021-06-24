package com.example.stayhealthy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stayhealthy.data.models.domain.Food
import com.example.stayhealthy.repositories.FoodRepository
import com.firebase.ui.database.FirebaseRecyclerOptions


private const val TAG = "FoodMenuViewModel"

class FoodMenuViewModel(private val foodRepository: FoodRepository) : ViewModel() {

    private val _currentFoodMLD = MutableLiveData<FirebaseRecyclerOptions<Food>>()
    val currentFoodLD: LiveData<FirebaseRecyclerOptions<Food>>
        get() = _currentFoodMLD


    fun getFoodOptionsCategory(category: String) {
        Log.d(TAG, "getFoodOptionsFromFirebase: starts with $category")

        val query = foodRepository.createFoodQuery(category)
        val options = FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food::class.java)
                .build() // creating configurations for firebase adapter
        _currentFoodMLD.value = options

        Log.d(TAG, "getFoodOptionsFromFirebase: ends with $category, $query, $options")

    }


    fun getFoodOptionsSearchCondition(category: String, searchCondition: String) {

        Log.d(
                TAG,
                "getFoodOptionsSearchCondition: starts with $category, condition $searchCondition"
        )
        val query = foodRepository.createFoodQuerySearchCondition(category, searchCondition)
        val options = FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food::class.java).build()
        _currentFoodMLD.value = options

        Log.d(TAG, "getFoodOptionsSearchCondition: ends with $category, $query, $options")

    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: starts ")
        super.onCleared()
        Log.d(TAG, "onCleared: ends ")
    }

}