package com.example.stayhealthy.ui.foodMenu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayhealthy.model.Food
import com.example.stayhealthy.repository.FoodRepository
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.coroutines.launch


private const val TAG = "FoodMenuViewModel"

class FoodMenuViewModel(var foodRepository: FoodRepository) : ViewModel() {

    private val _currentFoodMLD= MutableLiveData<FirebaseRecyclerOptions<Food>>()
    val currentFoodLD: LiveData<FirebaseRecyclerOptions<Food>>
        get() = _currentFoodMLD


    fun getFoodOptionsFrom(category: String)  {
        Log.d(TAG, "getFoodOptionsFromFirebase: starts with $category")

        val query = foodRepository.createFoodQuery(category)
        val options = FirebaseRecyclerOptions.Builder<Food>()
                    .setQuery(query, Food::class.java).build() // creating configurations for firebase adapter
        _currentFoodMLD.value = options

        Log.d(TAG, "getFoodOptionsFromFirebase: ends with $category, $query, $options")

    }


    fun getFoodOptionsSearchCondition(category: String, searchCondition: String)  {

        Log.d(TAG, "getFoodOptionsSearchCondition: starts with $category, condition $searchCondition")
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