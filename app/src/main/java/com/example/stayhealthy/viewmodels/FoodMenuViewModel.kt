package com.example.stayhealthy.viewmodels

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stayhealthy.data.models.domain.Food
import com.example.stayhealthy.data.models.domain.UserFood
import com.example.stayhealthy.repositories.FoodRepository
import com.example.stayhealthy.repositories.UserRepository
import com.example.stayhealthy.ui.forms.AddFoodForm
import com.example.stayhealthy.util.Result
import com.example.stayhealthy.util.SingleEventLiveData
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


private const val TAG = "FoodMenuViewModel"

class FoodMenuViewModel(
        private val foodRepository: FoodRepository,
        private val userRepository: UserRepository
) : ViewModel() {

    private val _currentFoodOptionsMLD = MutableLiveData<FirebaseRecyclerOptions<Food>>()
    val currentFoodOptionsLD: LiveData<FirebaseRecyclerOptions<Food>>
        get() = _currentFoodOptionsMLD

    val isUserFoodMLD = MutableLiveData(false)
    val isUserFoodLD: LiveData<Boolean>
        get() = isUserFoodMLD

    private val currentUser = userRepository.user

    var photoFoodFile: File? = null

    val addFoodForm = AddFoodForm()

    val toast = SingleEventLiveData<String?>()

    var spinnerSelectedFoodCategory = MutableLiveData<String>()

    var tabSelectedFoodCategoryMLD = MutableLiveData<String>()
    val tabSelectedFoodCategoryLD: LiveData<String>
        get() = tabSelectedFoodCategoryMLD


    private val isLoadingMLD = MutableLiveData(false)
    val isLoadingLD: LiveData<Boolean>
        get() = isLoadingMLD

    val onFoodCategorySelected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            spinnerSelectedFoodCategory.value = parent?.getItemAtPosition(position).toString()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    fun getFoodOptionsCategory() {
        Log.d(TAG, "getFoodOptionsFromFirebase: starts with ${tabSelectedFoodCategoryMLD.value}")

        tabSelectedFoodCategoryMLD.value?.let { category ->
            val query = if (isUserFoodMLD.value == false) {
                foodRepository.createFoodQuery(category)
            } else {
                foodRepository.createUserFoodQuery(currentUser.id, category)
            }
            val options = FirebaseRecyclerOptions.Builder<Food>()
                    .setQuery(query, Food::class.java)
                    .build() // creating configurations for firebase adapter
            _currentFoodOptionsMLD.value = options

        }
    }


    fun getFoodOptionsSearchCondition(searchCondition: String) {

        Log.d(
                TAG,
                "getFoodOptionsSearchCondition: starts with ${tabSelectedFoodCategoryMLD.value}, condition $searchCondition"
        )

        tabSelectedFoodCategoryMLD.value?.let { category ->

            val query = if (isUserFoodMLD.value == false) {
                foodRepository.createFoodQuerySearchCondition(category, searchCondition)

            } else {
                foodRepository.createUserFoodQuerySearchCondition(
                        currentUser.id,
                        category,
                        searchCondition
                )
            }

            val options = FirebaseRecyclerOptions.Builder<Food>()
                    .setQuery(query, Food::class.java).build()
            _currentFoodOptionsMLD.value = options

            Log.d(TAG, "getFoodOptionsSearchCondition: ends with $category, $query, $options")
        }
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: starts ")
        super.onCleared()
        Log.d(TAG, "onCleared: ends ")
    }


    fun createImageFileFood() {
        photoFoodFile = foodRepository.createFoodImageFile()
    }

    suspend fun addUserFood(uri: Uri) {
        val userFoodRequest = addFoodForm.request
        var userFood: UserFood
        userFoodRequest.apply {
            userFood = UserFood(
                    name = name,
                    quantity = quantity,
                    calories = calories,
                    category = spinnerSelectedFoodCategory.value!!
            )
        }

        isLoadingMLD.value = true
        when (val result = withContext(Dispatchers.IO) {
            foodRepository.addUserFood(
                    userRepository.getLocalUserAsync().id,
                    userFood,
                    uri
            )
        }.also { isLoadingMLD.postValue(false) }) {
            is Result.Success -> {
                Log.d(
                        TAG,
                        "addUserFood is Result.Success"
                )
            }
            is Result.Error -> {
                Log.e(TAG, "${result.exception.message}")
                toast.value = result.exception.message
            }
            is Result.Canceled -> {
                Log.e(TAG, "${result.exception!!.message}")
                toast.value = "Request canceled"
            }
        }
    }
}