package com.example.stayhealthy.ui.profile

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayhealthy.CalculationMethods
import com.example.stayhealthy.R
import com.example.stayhealthy.model.User
import com.example.stayhealthy.repository.UserRepository
import com.example.stayhealthy.utils.Result
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


private const val TAG = "ProfileViewModel"


class ProfileViewModel(var userRepository: UserRepository) : ViewModel() {

    private val _currentUserMLD = MutableLiveData<User?>()
    val currentUserLD: LiveData<User?>
        get() = _currentUserMLD

    private val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast

    private val _currentUserCalorieNeedsMLD = MutableLiveData<Int>()
    val currentUserCalorieNeedsLD: LiveData<Int>
        get() = _currentUserCalorieNeedsMLD

    private val _currentUserBMIMLD = MutableLiveData<Double>()
    val currentUserBMILD: LiveData<Double>
        get() = _currentUserBMIMLD

    init {
        viewModelScope.launch {
            val currentFirebaseUser = checkUserLoggedIn()
            val user = getUserFromFirestore(currentFirebaseUser!!.uid)
            if(user != null) {
                getUserCalorieNeeds(user)
                getUserBMI(user)
            }
        }
    }

    private suspend fun getUserFromFirestore(userId: String): User?
    {
        Log.d(TAG, "getUserFromFirestore: starts with $userId")

        var mUser : User? = null
        when (val result = withContext(IO){userRepository.getUserFromFirestore(userId) }) { //get user in background thread since profile fragment has some performance issue when creating
            is Result.Success -> {
                mUser = result.data
                _currentUserMLD.value = mUser
                Log.d(TAG, "getUserFromFirestore is Result.Success, user is ${_currentUserMLD.value}")

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

        Log.d(TAG, "getUserFromFirestore: ends, user is ${_currentUserMLD.value?.id}")
        return mUser
    }



    suspend fun updateUserInFirestore(user: User, activity: Activity) { // firestore will save in local cache if there is no connection
        Log.d(TAG, "updateUserInFirestore starts with - $user")
        viewModelScope.launch {
            when(val result = userRepository.updateUserInFirestore(user))
            {
                is Result.Success -> {
                    Log.d(TAG, "updateUserInFirestore is Result.Success - $user")
                    _currentUserMLD.value = user

                }
                is Result.Error -> {
                    _toast.value = result.exception.message
                }
                is Result.Canceled -> {
                    _toast.value = activity.getString(R.string.request_canceled)
                }
            }
        }
        Log.d(TAG, "updateUserInFirestore ends with - $_currentUserMLD")
    }


    private suspend fun checkUserLoggedIn(): FirebaseUser?
    {
        Log.d(TAG, "checkUserLoggedIn: starts")

        val mFirebaseUser = userRepository.checkUserLoggedIn()

        Log.d(TAG, "checkUserLoggedIn: ends, user is ${mFirebaseUser?.uid}")
        return mFirebaseUser
    }


    fun getUserCalorieNeeds(user: User)  {
            Log.d(TAG, "getUserCalorieNeeds: starts")
            val calculationMethods = CalculationMethods(user)
            _currentUserCalorieNeedsMLD.value = calculationMethods.calculateCaloriesForOptimizingBMI()
            Log.d(TAG, "getUserCalorieNeeds: ends with ${_currentUserCalorieNeedsMLD.value}")

    }


    fun getUserBMI(user: User) {
        Log.d(TAG, "getUserBMI: starts")
        val calculationMethods = CalculationMethods(user)
        _currentUserBMIMLD.value = calculationMethods.calculateBMI()
        Log.d(TAG, "getUserBMI: ends with ${_currentUserBMIMLD.value}")

    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: starts")
        super.onCleared()
        Log.d(TAG, "onCleared: ends")
    }

    fun onToastShown()
    {
        _toast.value = null
    }

}