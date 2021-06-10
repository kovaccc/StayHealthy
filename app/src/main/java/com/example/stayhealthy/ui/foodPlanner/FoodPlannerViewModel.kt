package com.example.stayhealthy.ui.foodPlanner

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.example.stayhealthy.CalculationMethods
import com.example.stayhealthy.dialogs.DATE_DEFAULT
import com.example.stayhealthy.model.MealPlanItem
import com.example.stayhealthy.model.User
import com.example.stayhealthy.module.DATE_MEAL_PLAN
import com.example.stayhealthy.repository.*
import com.example.stayhealthy.utils.Result
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.example.stayhealthy.repository.MenuContract
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "FoodPlannerViewModel"
class FoodPlannerViewModel(private val dateSharedPreferences: SharedPreferences, var mealPlanRepository: MealPlanRepository, var userRepository: UserRepository) : ViewModel() {


    private val _currentMealPlanMLD = MutableLiveData<FirestoreRecyclerOptions<MealPlanItem>>()
    val currentMealPlanLD: LiveData<FirestoreRecyclerOptions<MealPlanItem>>
        get() = _currentMealPlanMLD


    private val _selectedDateMLD = MutableLiveData<Long>() // working with Long values since it is preferred way for storing Date in database
    val selectedDateLD: LiveData<Long>
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


    private var _vegetableMLD= MutableLiveData<String>()
    val vegetableLD: LiveData<String>
        get() = _vegetableMLD

    private var _mealCaloriesMLD = MutableLiveData<String>()
    val mealCaloriesLD: LiveData<String>
        get() = _mealCaloriesMLD



    private val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast


    // using this for logs
    private val myFormat = "EEEE, dd/MM/yy, hh:mm:ss"
    private val sdf = SimpleDateFormat(myFormat, Locale.getDefault())


    private var currentUser: User?  = null

    
    private val dateListener = SharedPreferences.OnSharedPreferenceChangeListener{sharedPreferences, key -> when(key) {
        DATE_MEAL_PLAN -> {
                val date = Date()
                date.time = sharedPreferences.getLong(key, DATE_DEFAULT)

                Log.d(TAG, "dateListener: new date is ${date.time} and old date is ${_selectedDateMLD.value}")

                _selectedDateMLD.value = date.time
                getMealPlanOptionsFromFirestore()
                updateCalories()

                Log.d(TAG, "dateListener: now date is ${_selectedDateMLD.value}")
            }
        }
    }

    init {
        _selectedDateMLD.value = dateSharedPreferences.getLong(DATE_MEAL_PLAN, DATE_DEFAULT)
        dateSharedPreferences.registerOnSharedPreferenceChangeListener(dateListener)
        loadMealPlan()
    }


    private fun loadMealPlan() {
        viewModelScope.launch { //coroutine getMealPlanOptionsFromFirestore and updateCalories wait for checkUserLoggedIn and getUserFromFirestore result
            if(currentUser == null) {
                val firebaseUser = checkUserLoggedIn()
                currentUser = getUserFromFirestore(firebaseUser!!.uid)
            }
            if(currentUser != null) {
                getMealPlanOptionsFromFirestore()
                updateCalories()
            }
        }
    }

    private fun getMealPlanOptionsFromFirestore()  {

        viewModelScope.launch { // start new coroutine to execute so updateCalories can be executed async
            Log.d(TAG, "getMealPlanOptionsFromFirestore: starts with $currentUser, date ${_selectedDateMLD.value!!}")

            val startTime = getStartTimeOfDate(_selectedDateMLD.value!!)
            val endTime = getEndTimeOfDate(_selectedDateMLD.value!!)

            Log.d(TAG, "getMealPlanOptionsFromFirestore: between day ${sdf.format(startTime)}, and ${sdf.format(endTime)}")

            val query = mealPlanRepository.createMealPlanQuery(currentUser!!.id, startTime, endTime)
            val options = FirestoreRecyclerOptions.Builder<MealPlanItem>()
                    .setQuery(query, MealPlanItem::class.java).build() // creating configurations for firestore adapter

            Log.d(TAG, "getMealPlanOptionsFromFirestore: ends with $options")

            _currentMealPlanMLD.value = options

        }
    }

    private fun getStartTimeOfDate(date: Long) : Long {

        val calendar = GregorianCalendar()

        calendar.timeInMillis = date
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0)
        calendar.set(GregorianCalendar.MINUTE, 0)
        calendar.set(GregorianCalendar.SECOND, 0)
        calendar.set(GregorianCalendar.MILLISECOND, 0)
        return calendar.timeInMillis

    }


    private fun getEndTimeOfDate(date: Long): Long {

        val calendar = GregorianCalendar()

        calendar.timeInMillis = date
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 23)
        calendar.set(GregorianCalendar.MINUTE, 59)
        calendar.set(GregorianCalendar.SECOND, 59)
        calendar.set(GregorianCalendar.MILLISECOND, 59) // needed to put milliseconds because storing objects in milliseconds

        return calendar.timeInMillis


    }


    private suspend fun getUserFromFirestore(userId: String) : User?
    {
        Log.d(TAG, "getUserFromFirestore: starts with $userId")

        var mUser : User? = null
        when (val result = withContext(IO){userRepository.getUserFromFirestore(userId)}) { // best practice is to put withContext(IO) in repository class but it looks clean in this app's ViewModels
            is Result.Success -> {

                mUser = result.data
                Log.d(TAG, "${result.data}")
                Log.d(TAG, "getUserFromFirestore is Result.Success, user is $mUser")

            }
            is Result.Error -> {
                Log.e(TAG, "${result.exception.message}")
                _toast.value = result.exception.message
            }
            is Result.Canceled -> {
                Log.e(TAG, "${result.exception!!.message}")
                _toast.value = "Request canceled"
            }
        }
        Log.d(TAG, "getUserFromFirestore: ends, user is $mUser")
        return mUser
    }

    private suspend fun checkUserLoggedIn(): FirebaseUser?
    {
        Log.d(TAG, "checkUserLoggedIn: starts")
        val firebaseUser = withContext(IO){userRepository.checkUserLoggedIn()}
        Log.d(TAG, "checkUserLoggedIn: ends, user is ${firebaseUser?.uid}")
        return firebaseUser
    }



    private suspend fun getMealPlanFromFirestore(userId: String, date: Long) :  ArrayList<MealPlanItem> {

        Log.d(TAG, "getMealPlanFromFirestore: starts with $userId AND $date")

        var meals = ArrayList<MealPlanItem>()


        val startTime = getStartTimeOfDate(date)
        val endTime = getEndTimeOfDate(date)

        when (val result = withContext(IO){mealPlanRepository.getMealPlanQuery(userId, startTime,endTime)}) { //background thread for heavy network operation
            is Result.Success -> {
                meals = result.data!!

                Log.d(TAG, "getMealPlanFromFirestore is Result.Success, meal plan is ${result.data}")

            }
            is Result.Error -> {
                Log.e(TAG, "${result.exception.message}")
                _toast.value = result.exception.message
            }
            is Result.Canceled -> {
                Log.e(TAG, "${result.exception!!.message}")
                _toast.value = "Request canceled"
            }
        }

        Log.d(TAG, "getMealPlanFromFirestore: ends, meal plan is $meals")
        return meals
    }

     fun updateCalories() {

        Log.d(TAG, "updateCalories: starts with $currentUser and ${_selectedDateMLD.value!!}")
        viewModelScope.launch {
            val meals = getMealPlanFromFirestore(currentUser!!.id, _selectedDateMLD.value!!)
            mealPlanCalories(meals)
        }
        Log.d(TAG, "updateCalories: ends with $currentUser and ${_selectedDateMLD.value!!}")
    }

    private suspend fun mealPlanCalories(meals : ArrayList<MealPlanItem>?) {
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

                val totalCalories = fruitsCalories + vegetablesCalories + grainsPastaCalories + proteinsCalories
                val calculationMethods = CalculationMethods(currentUser!!)
                _vegetableMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.VEGETABLES_NODE_NAME) - vegetablesCalories).toString())
                _fruitsMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.FRUITS_NODE_NAME) - fruitsCalories).toString())
                _proteinsMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.PROTEINS_NODE_NAME) - proteinsCalories).toString())
                _grainsPastaMLD.postValue((calculationMethods.calculateFoodCalorieNeeds(MenuContract.GRAINS_PASTA_NODE_NAME) - grainsPastaCalories).toString())
                _mealCaloriesMLD.postValue((calculationMethods.calculateCaloriesForOptimizingBMI() - totalCalories).toString())

            }
            Log.d(TAG, "mealPlanCalories: ends, values are fruits: $fruitsCalories, vegetables $vegetablesCalories, proteins $proteinsCalories and grains&pasta $grainsPastaCalories")
        }
        Log.d(TAG, "mealPlanCalories: ends")
    }


    //TODO put withContext(IO) before mealplanrepository you dont want this on main thread and according to documentation flow runs on coroutine parent context
    private suspend fun subscribeMealPlanForChanges(userId: String, date: Long) {

        Log.d(TAG, "subscribeMealPlanForChanges starts with $userId and date $date")

        val startTime = getStartTimeOfDate(date)
        val endTime = getEndTimeOfDate(date)

            mealPlanRepository.listenOnMealPlanChanged(userId,startTime,endTime).collect { result->

                when (result) {
                    is Result.Success -> {
                        val meals = result.data!!
                        mealPlanCalories(meals)
                        Log.d(TAG, "subscribeMealPlanForChanges is Result.Success, meal plan is ${result.data}")

                    }
                    is Result.Error -> {
                        Log.e(TAG, "${result.exception.message}")
                        _toast.value = result.exception.message
                    }
                    is Result.Canceled -> {
                        Log.e(TAG, "${result.exception!!.message}")
                        _toast.value = "Request canceled"
                    }
                }

            }
    }


        fun onToastShown()
    {
        _toast.value = null
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: starts")
        super.onCleared()
        dateSharedPreferences.unregisterOnSharedPreferenceChangeListener(dateListener)
        Log.d(TAG, "onCleared: ends")
    }

}