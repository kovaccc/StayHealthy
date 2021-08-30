package com.example.stayhealthy.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.example.stayhealthy.R
import com.example.stayhealthy.common.CalculationMethods
import com.example.stayhealthy.data.models.domain.User
import com.example.stayhealthy.data.models.persistance.DBUser
import com.example.stayhealthy.repositories.UserRepository
import com.example.stayhealthy.util.Result
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


private const val TAG = "ProfileViewModel"


class ProfileViewModel(var userRepository: UserRepository) : ViewModel() {


    val currentUserLD: LiveData<DBUser?> = userRepository.getLocalUserLiveData().asLiveData()

    private val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast

    private val isLoadingMLD = MutableLiveData<Boolean>(false)
    val isLoadingLD: LiveData<Boolean>
        get() = isLoadingMLD


    private val _currentUserCalorieNeedsMLD = MutableLiveData<Int>()
    val currentUserCalorieNeedsLD: LiveData<Int>
        get() = _currentUserCalorieNeedsMLD

    private val _currentUserBMIMLD = MutableLiveData<Double>()
    val currentUserBMILD: LiveData<Double>
        get() = _currentUserBMIMLD

    init {
        viewModelScope.launch {
            val currentFirebaseUser = checkUserLoggedIn()
            currentFirebaseUser?.let { getUserFromFirestore(it.uid) }
            val currentUser = userRepository.getLocalUserAsync()
            getUserCalorieNeeds(currentUser)
            getUserBMI(currentUser)
        }
    }

    private suspend fun getUserFromFirestore(userId: String) {
        Log.d(TAG, "getUserFromFirestore: starts with $userId")

        when (val result = withContext(IO) { userRepository.getUserFromFirestore(userId) }) { //get user in background thread since profile fragment has some performance issue when creating
            is Result.Success -> {
                Log.d(TAG, "getUserFromFirestore is Result.Success, user is ${currentUserLD.value}")
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

        Log.d(TAG, "getUserFromFirestore: ends, user is ${currentUserLD.value?.id}")
    }


    suspend fun updateUserInFirestore(user: User, activity: Activity) {
        Log.d(TAG, "updateUserInFirestore starts with - $user")
        viewModelScope.launch {

            isLoadingMLD.value = true

            when (val result = withContext(IO) { userRepository.updateUserInFirestore(user).also { isLoadingMLD.postValue(false)  } }) // switch to IO dispatcher for network call
            {
                is Result.Success -> {
                    Log.d(TAG, "updateUserInFirestore is Result.Success - $user")
                }
                is Result.Error -> {
                    _toast.value = result.exception.message
                }
                is Result.Canceled -> {
                    _toast.value = activity.getString(R.string.request_canceled)
                }
            }
        }
        Log.d(TAG, "updateUserInFirestore ends with - $currentUserLD")
    }


    private suspend fun checkUserLoggedIn(): FirebaseUser? {
        Log.d(TAG, "checkUserLoggedIn: starts")

        val mFirebaseUser = withContext(IO) { userRepository.checkUserLoggedIn() }

        Log.d(TAG, "checkUserLoggedIn: ends, user is ${mFirebaseUser?.uid}")
        return mFirebaseUser
    }


    fun getUserCalorieNeeds(user: User) {
        Log.d(TAG, "getUserCalorieNeeds: starts")
        val calculationMethods = CalculationMethods(user)
        _currentUserCalorieNeedsMLD.value = calculationMethods.calculateCaloriesForOptimizingBMI()
        Log.d(TAG, "getUserCalorieNeeds: ends with ${_currentUserCalorieNeedsMLD.value}")

    }

//    private suspend fun subscribeUserForChanges(userId: String) { // changing data in firebase or another device will reflect on liveData
//
//        Log.d(TAG, "subscribeUser: starts with $userId")
//        userRepository.listenOnUserChanged(userId).collect { result ->
//            when (result) {
//                is Result.Success -> {
//                    val mUser = result.data
//                    _currentUserMLD.value = mUser
//                    Log.d(TAG, "subscribeUser is Result.Success, user is ${_currentUserMLD.value}")
//                }
//                is Result.Error -> {
//                    Log.e(TAG, "${result.exception.message}")
//                    _toast.value = result.exception.message
//                }
//                is Result.Canceled -> {
//                    Log.e(TAG, "${result.exception!!.message}")
//                    _toast.value = "Request canceled"
//                }
//            }
//        }
//        Log.d(TAG, "subscribeUser: ends, user is ${_currentUserMLD.value}")
//    }


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

    fun onToastShown() {
        _toast.value = null
    }

}