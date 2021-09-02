package com.example.stayhealthy.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.example.stayhealthy.common.CalculationMethods
import com.example.stayhealthy.ui.dialogs.DATE_DEFAULT
import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.example.stayhealthy.repositories.*
import com.example.stayhealthy.util.Result
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.example.stayhealthy.common.contracts.MenuContract
import com.example.stayhealthy.config.KEY_DATE_MEAL_PLAN
import com.example.stayhealthy.data.PrefsHelper
import com.example.stayhealthy.util.TimeHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "FoodPlannerViewModel"

class FoodPlannerViewModel(
        private val dateSharedPreferences: SharedPreferences,
        prefsHelper: PrefsHelper,
        private val mealPlanRepository: MealPlanRepository,
        private val userRepository: UserRepository
) : ViewModel() {

    private val _currentMealPlanMLD = MutableLiveData<FirestoreRecyclerOptions<MealPlanItem>>()
    val currentMealPlanLD: LiveData<FirestoreRecyclerOptions<MealPlanItem>>
        get() = _currentMealPlanMLD

    private val _selectedDateMLD =
            MutableLiveData<Long?>()
    val selectedDateLD: LiveData<Long?>
        get() = _selectedDateMLD

    private val _fruitsMLD = MutableLiveData<String>()
    val fruitsLD: LiveData<String>
        get() = _fruitsMLD

    private val _proteinsMLD = MutableLiveData<String>()
    val proteinsLD: LiveData<String>
        get() = _proteinsMLD

    private val _grainsPastaMLD = MutableLiveData<String>()
    val grainsPastaLD: LiveData<String>
        get() = _grainsPastaMLD

    private var _vegetableMLD = MutableLiveData<String>()
    val vegetableLD: LiveData<String>
        get() = _vegetableMLD

    private var _mealCaloriesMLD = MutableLiveData<String>()
    val mealCaloriesLD: LiveData<String>
        get() = _mealCaloriesMLD

    private val toastMLD = MutableLiveData<String?>()
    val toastLD: LiveData<String?>
        get() = toastMLD

    // using this for logs
    private val myFormat = "EEEE, dd/MM/yy, hh:mm:ss"
    private val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    private val currentUser = userRepository.user

    private val dateListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                when (key) {
                    KEY_DATE_MEAL_PLAN -> {
                        val date = Date()
                        date.time = sharedPreferences.getLong(key, DATE_DEFAULT)

                        Log.d(
                                TAG,
                                "dateListener: new date is ${date.time} and old date is ${_selectedDateMLD.value}"
                        )

                        _selectedDateMLD.value = date.time
                        if (userRepository.getUserNullable() != null) {
                            loadMealPlan()
                        }

                        Log.d(TAG, "dateListener: now date is ${_selectedDateMLD.value}")
                    }
                }
            }

    init {
        _selectedDateMLD.value = prefsHelper.getSelectedMealPlanDate()
        dateSharedPreferences.registerOnSharedPreferenceChangeListener(dateListener)
        loadMealPlan()
    }


    private fun loadMealPlan() {
        getMealPlanOptionsFromFirestore()
        updateCalories()
    }

    private fun getMealPlanOptionsFromFirestore() {

        viewModelScope.launch {
            Log.d(
                    TAG,
                    "getMealPlanOptionsFromFirestore: starts with $currentUser, date ${_selectedDateMLD.value!!}"
            )

            val startTime = TimeHelper.getStartTimeOfDate(_selectedDateMLD.value!!)
            val endTime = TimeHelper.getEndTimeOfDate(_selectedDateMLD.value!!)

            Log.d(
                    TAG,
                    "getMealPlanOptionsFromFirestore: between day ${sdf.format(startTime)}, and ${
                        sdf.format(endTime)
                    }, id ${currentUser.id}"
            )

            val query = mealPlanRepository.createMealPlanQuery(currentUser.id, startTime, endTime)
            val options = FirestoreRecyclerOptions.Builder<MealPlanItem>()
                    .setQuery(query, MealPlanItem::class.java)
                    .build() // creating configurations for firestore adapter

            Log.d(TAG, "getMealPlanOptionsFromFirestore: ends with $options")

            _currentMealPlanMLD.value = options

        }
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
                toastMLD.value = result.exception.message
            }
            is Result.Canceled -> {
                Log.e(TAG, "${result.exception!!.message}")
                toastMLD.value = "Request canceled"
            }
        }

        Log.d(TAG, "getMealPlanFromFirestore: ends, meal plan is $meals")
        return meals
    }

    fun updateCalories() {

        Log.d(TAG, "updateCalories: starts with $currentUser and ${_selectedDateMLD.value!!}")
        viewModelScope.launch {
            val meals = getMealPlanFromFirestore(userRepository.getLocalUserAsync().id, _selectedDateMLD.value!!)
            mealPlanCalories(meals)
        }
        Log.d(TAG, "updateCalories: ends with $currentUser and ${_selectedDateMLD.value!!}")
    }

    private suspend fun mealPlanCalories(meals: ArrayList<MealPlanItem>?) {
        Log.d(TAG, "mealPlanCalories: starts")

        withContext(Default) { // Heavy work so it is done in different thread,Default used for complex operations like list traversal or mathematical operations
            var fruitsCalories = 0
            var vegetablesCalories = 0
            var grainsPastaCalories = 0
            var proteinsCalories = 0

            if (meals != null) {

                meals.forEach { mealPlanItem ->
                    Log.d(TAG, "mealPlanCalories: mealPlanItem object is $mealPlanItem")
                    when (mealPlanItem.category) {
                        MenuContract.FRUITS_NODE_NAME -> fruitsCalories += mealPlanItem.calories.toInt()
                        MenuContract.VEGETABLES_NODE_NAME -> vegetablesCalories += mealPlanItem.calories.toInt()
                        MenuContract.PROTEINS_NODE_NAME -> proteinsCalories += mealPlanItem.calories.toInt()
                        MenuContract.GRAINS_PASTA_NODE_NAME -> grainsPastaCalories += mealPlanItem.calories.toInt()
                    }
                }

                val totalCalories =
                        fruitsCalories + vegetablesCalories + grainsPastaCalories + proteinsCalories
                val calculationMethods = CalculationMethods(currentUser)
                _vegetableMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.VEGETABLES_NODE_NAME) - vegetablesCalories).toString())
                _fruitsMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.FRUITS_NODE_NAME) - fruitsCalories).toString())
                _proteinsMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.PROTEINS_NODE_NAME) - proteinsCalories).toString())
                _grainsPastaMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.GRAINS_PASTA_NODE_NAME) - grainsPastaCalories).toString())
                _mealCaloriesMLD.postValue((calculationMethods.calculateCaloriesForOptimizingBMI() - totalCalories).toString())

            }
            Log.d(
                    TAG,
                    "mealPlanCalories: ends, values are fruits: $fruitsCalories, vegetables $vegetablesCalories, proteins $proteinsCalories and grains&pasta $grainsPastaCalories"
            )
        }
        Log.d(TAG, "mealPlanCalories: ends")
    }


//    private fun subscribeMealPlanForChanges(userId: String, date: Long) {
//
//        Log.d(TAG, "subscribeMealPlanForChanges starts with $userId and date $date")
//
//        resetSubscriptionScope()
//
//        subscriptionScope.launch(errorHandler) {
//            val startTime = getStartTimeOfDate(date)
//            val endTime = getEndTimeOfDate(date)
//
//            mealPlanRepository.listenOnMealPlanChanged(userId, startTime, endTime)
//                    .collect { result ->
//
//                        when (result) {
//                            is Result.Success -> {
//                                val meals = result.data!!
//                                mealPlanCalories(meals)
//                                Log.d(
//                                        TAG,
//                                        "subscribeMealPlanForChanges is Result.Success, meal plan is ${result.data}"
//                                )
//
//                            }
//                            is Result.Error -> {
//                                Log.e(TAG, "${result.exception.message}")
//                                toastMLD.value = result.exception.message
//                            }
//                            is Result.Canceled -> {
//                                Log.e(TAG, "${result.exception!!.message}")
//                                toastMLD.value = "Request canceled"
//                            }
//                        }
//
//                    }
//        }
//    }
//


    fun onToastShown() {
        toastMLD.value = null
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: starts")
        super.onCleared()
        dateSharedPreferences.unregisterOnSharedPreferenceChangeListener(dateListener)
        Log.d(TAG, "onCleared: ends")
    }

}