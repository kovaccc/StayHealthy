package com.example.stayhealthy.ui

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayhealthy.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.example.stayhealthy.utils.Result
import com.example.stayhealthy.R
import com.example.stayhealthy.createLoadingDialog
import com.example.stayhealthy.repository.UserRepository
import java.lang.Exception


private const val TAG = "UserViewModel"
class UserViewModel(var userRepository: UserRepository): ViewModel()
{
    private var callbackManager: CallbackManager? = null

    private lateinit var googleSingInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1

    private val _toast = MutableLiveData<String?>()
    val toast: LiveData<String?>
        get() = _toast

    private val _spinner = MutableLiveData(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _currentUserMLD = MutableLiveData<User?>()
    val currentUserLD: LiveData<User?>
        get() = _currentUserMLD


    var mFirebaseUser : User? = null // for checking logs

    fun registerUserFromAuthWithEmailAndPassword(name: String, email: String, password: String, activity: Activity)
    {

        Log.d(TAG, "registerUserFromAuthWithEmailAndPassword starts")

        launchDataLoad {
            viewModelScope.launch {

                val dialog = createLoadingDialog(activity)
                dialog.show()


                when(val result = userRepository.registerUserFromAuthWithEmailAndPassword(email, password, activity.applicationContext))
                {
                    is Result.Success -> {
                        Log.e(TAG, "Result.Success")
                        result.data?.let {firebaseUser ->

                            createUserInFirestore(createUserObject(firebaseUser, name), activity)

                            _toast.value = activity.getString(R.string.registration_successful)

                            mFirebaseUser = _currentUserMLD.value

                            startActivity(activity, ParameterActivity())

                            Log.d(TAG, "registerUserFromAuthWithEmailAndPassword is Result.Success: Result user is $mFirebaseUser")
                        }
                    }
                    is Result.Error -> {
                        Log.e(TAG, "${result.exception.message}")
                        _toast.value = result.exception.message
                    }
                    is Result.Canceled -> {
                        Log.e(TAG, "${result.exception!!.message}")
                        _toast.value = activity.getString(R.string.request_canceled)
                    }
                }

                dialog.dismiss()
            }

        }

        Log.d(TAG, "registerUserFromAuthWithEmailAndPassword ends: Result user is $mFirebaseUser")

    }

    private fun startActivity(startingActivity: Activity, launchActivity: Activity)
    {
        Log.d(TAG, "startActivity starts : starting activity from $startingActivity to $launchActivity")

        val intent = Intent(startingActivity, launchActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startingActivity.startActivity(intent)

        Log.d(TAG, "startActivity ends: starting activity from $startingActivity to $launchActivity")
    }




    suspend fun getUserFromFirestore(userId: String, activity: Activity)
    {
        Log.d(TAG, "getUserFromFirestore: starts with $userId")

        when (val result = userRepository.getUserFromFirestore(userId)) {
            is Result.Success -> {

               val mUser = result.data
                Log.d(TAG, "${result.data}")
                _currentUserMLD.value = mUser

                Log.d(TAG, "getUserFromFirestore is Result.Success, user is ${_currentUserMLD.value?.id}")

            }
            is Result.Error -> {
                Log.e(TAG, "${result.exception.message}")
                _toast.value = result.exception.message
            }
            is Result.Canceled -> {
                Log.e(TAG, "${result.exception!!.message}")
                _toast.value = activity.getString(R.string.request_canceled)
            }
        }
        Log.d(TAG, "getUserFromFirestore: ends, user is ${_currentUserMLD.value?.id}")
    }





    fun checkUserLoggedIn(): FirebaseUser?
    {
        Log.d(TAG, "checkUserLoggedIn: starts")
        var mFirebaseUser: FirebaseUser? = null
        viewModelScope.launch {

            mFirebaseUser = userRepository.checkUserLoggedIn()

        }
        Log.d(TAG, "checkUserLoggedIn: ends, user is ${mFirebaseUser?.uid}")
        return mFirebaseUser
    }


    private fun clearUser() {
        Log.d(TAG, "clearUser: starts with user ${_currentUserMLD.value}")
        _currentUserMLD.postValue(null)
        Log.d(TAG, "clearUser: ends with user ${_currentUserMLD.value}")
    }

    fun logOutUser()
    {
        Log.d(TAG, "logOutUser: starts")
        viewModelScope.launch {
            userRepository.logOutUser()
            clearUser()
        }
        Log.d(TAG, "logOutUser: ends")
    }


    fun createUserObject(firebaseUser: FirebaseUser, name: String): User
    {
        val currentUser = User(
            id =  firebaseUser.uid,
            name = name,
        )

        return currentUser
    }

    private suspend fun createUserInFirestore(user: User, activity: Activity)
    {
        Log.d(TAG, "createUserInFirestore starts with  - ${user.name}")
        when(val result = userRepository.createUserInFirestore(user))
        {
            is Result.Success -> {
                Log.d(TAG, activity::class.java.simpleName)
                Log.d(TAG, "createUserInFirestore is Result.Success - $user")
                _currentUserMLD.value = user

            }
            is Result.Error -> {
                _toast.value = result.exception.message
            }
            is Result.Canceled -> {
                _toast.value = activity.getString(R.string.request_canceled)
            }
        }
        Log.d(TAG, "createUserInFirestore ends: Result - ${_currentUserMLD.value}")
    }




    fun updateUserInFirestore(user: User, activity: Activity) {
        Log.d(TAG, "updateUserInFirestore starts with - $user")
        viewModelScope.launch {
            when (val result = userRepository.updateUserInFirestore(user)) {
                is Result.Success -> {
                    Log.d(TAG, activity::class.java.simpleName)

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


    fun logInUserFromAuthWithEmailAndPassword(email: String, password: String, activity: Activity)
    {

        Log.d(TAG, "logInUserFromAuthWithEmailAndPassword: starts")

        launchDataLoad {
            viewModelScope.launch {

                val dialog = createLoadingDialog(activity)
                dialog.show()

                when (val result = userRepository.logInUserFromAuthWithEmailAndPassword(email, password))
                {

                    is Result.Success -> {
                        Log.i(TAG, "SignIn - Result.Success - User: ${result.data}")
                        result.data?.let { firebaseUser ->


                            _toast.value = activity.getString(R.string.login_successful)

                            getUserFromFirestore(firebaseUser.uid, activity)

                            mFirebaseUser =  _currentUserMLD.value

                            Log.d(TAG, "logInUserFromAuthWithEmailAndPassword is Result.Success, user is $mFirebaseUser")
                            if(checkParametersFulfillment()) {
                                startActivity(activity, HomeActivity())
                            }

                            else {
                                startActivity(activity, ParameterActivity())
                            }
                        }
                    }
                    is Result.Error -> {
                        _toast.value = result.exception.message
                    }
                    is Result.Canceled -> {
                        _toast.value = activity.getString(R.string.request_canceled)
                    }

                }

                dialog.dismiss()

        Log.d(TAG, "logInUserFromAuthWithEmailAndPassword: ends, user is $mFirebaseUser")
            }
        }
    }
    fun checkParametersFulfillment() : Boolean
    {
        Log.d(TAG, "checkParametersFulfillment: starts with ${_currentUserMLD.value}")
        val isFilled = _currentUserMLD.value?.gender!!.isNotEmpty() && _currentUserMLD.value?.age != 0L  && _currentUserMLD.value?.height != 0L && _currentUserMLD.value?.weight != 0L && _currentUserMLD.value?.activityLevel!!.isNotEmpty()
        Log.d(TAG, "checkParametersFulfillment ends and it is $isFilled for ${_currentUserMLD.value}")
        return isFilled

    }


    fun sendPasswordResetEmail(email: String, activity: Activity)
    {
        viewModelScope.launch {
            when(val result = userRepository.sendPasswordResetEmail(email))
            {
                is Result.Success -> {
                    _toast.value = "Check email to reset your password!"
                }
                is Result.Error -> {
                    _toast.value = result.exception.message
                }
                is Result.Canceled -> {
                    _toast.value = activity.getString(R.string.request_canceled)
                }
            }
        }
    }

    //Facebook
    fun signInWithFacebook(activity: Activity)
    {
        Log.d(TAG, "signInWithFacebook: starts")
        launchDataLoad {

            callbackManager = CallbackManager.Factory.create()

            LoginManager
                    .getInstance()
                    .logInWithReadPermissions(
                            activity,
                            listOf("email", "public_profile")
                    )

            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>
            {

                override fun onSuccess(result: LoginResult?)
                {

                    val dialog = createLoadingDialog(activity)
                    dialog.show()

                    val credential = FacebookAuthProvider.getCredential(result?.accessToken?.token!!)
                    viewModelScope.launch {
                        when(val result = userRepository.signInWithCredential(credential))
                        {

                            is Result.Success -> {
                                Log.d(TAG, "Result.Success - ${result.data?.user?.uid}")
                                result.data?.user?.let { user ->


                                    val mUser = user.displayName?.let {
                                        createUserObject(
                                                user,
                                                it,
                                        )
                                    }

                                    mUser?.let {


                                        // first we are checking if this is register with facebook or login by checking if user exists in firestore
                                        getUserFromFirestore(mUser.id, activity)


                                        if(_currentUserMLD.value == null) {
                                            createUserInFirestore(mUser, activity)
                                            startActivity(activity, ParameterActivity())
                                        }

                                        else { // if user exists in firestore checking parameters fulfillment
                                            mFirebaseUser = _currentUserMLD.value

                                            if(checkParametersFulfillment()) {
                                                startActivity(activity, HomeActivity())
                                            }

                                            else {
                                                startActivity(activity, ParameterActivity())
                                            }

                                        }
                                    }

                                    Log.d(TAG, "signInWithFacebook is Result.Success with $user")
                                }
                            }
                            is Result.Error -> {
                                Log.e(TAG, "Result.Error - ${result.exception.message}")
                                _toast.value = result.exception.message
                            }
                            is Result.Canceled -> {
                                Log.d(TAG, "Result.Canceled")
                                _toast.value = activity.applicationContext.getString(R.string.request_canceled)
                            }
                        }

                        dialog.dismiss()
                    }

                }

                override fun onError(error: FacebookException?)
                {
                    Log.e(TAG, "Result.Error - ${error?.message}")
                    _toast.value = error?.message


                }

                override fun onCancel()
                {
                    Log.d(TAG, "Result.Canceled")
                    _toast.value = activity.applicationContext.getString(R.string.request_canceled)

                }
            })
        }


        Log.d(TAG, "signInWithFacebook ends with $mFirebaseUser")
    }




    //Google
     fun signInWithGoogle(activity: Activity)
    {
        Log.d(TAG, "signInWithGoogle starts")

        launchDataLoad {


            val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            googleSingInClient = GoogleSignIn.getClient(activity, googleSignInOptions)

            val intent = googleSingInClient.signInIntent
            activity.startActivityForResult(intent, RC_SIGN_IN)

            Log.d(TAG, "signInWithGoogle ends")

        }

    }
    private fun handleSignInResult (completedTask: Task<GoogleSignInAccount>, activity: Activity)
    {

        Log.d(TAG, "handleSignInResult starts")
        viewModelScope.launch {

            val dialog = createLoadingDialog(activity)
            dialog.show()

            try
            {

                val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
                account?.let {
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                    when(val result = userRepository.signInWithCredential(credential))
                    {
                        is Result.Success -> {
                            Log.d(TAG, "Result.Success - ${result.data?.user?.uid}")
                            result.data?.user?.let {user ->

                                val mUser = user.displayName?.let {
                                    createUserObject(
                                            user,
                                            it
                                    )

                                }

                                mUser?.let {


                                    getUserFromFirestore(user.uid, activity)

                                    if(_currentUserMLD.value == null) {
                                        createUserInFirestore(mUser, activity)
                                        startActivity(activity, ParameterActivity())

                                    }

                                    else { // if user exists in firestore checking parameters fullfilment
                                        mFirebaseUser = _currentUserMLD.value

                                        if(checkParametersFulfillment()) {
                                            startActivity(activity, HomeActivity())
                                        }

                                        else {
                                            startActivity(activity, ParameterActivity())
                                        }

                                    }

                                }
                            }

                            Log.d(TAG, "handleSignInResult is Result.Success with $mFirebaseUser")

                        }
                        is Result.Error -> {
                            Log.e(TAG, "Result.Error - ${result.exception.message}")
                            _toast.value = result.exception.message
                        }
                        is Result.Canceled -> {
                            Log.d(TAG, "Result.Canceled")
                            _toast.value = activity.getString(R.string.request_canceled)
                        }
                    }
                }


            }
            catch (exception: Exception)
            {
                Toast.makeText(activity.applicationContext, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()

        }

        Log.d(TAG, "handleSignInResult ends")

    }



    fun onToastShown()
    {
        _toast.value = null
    }



    private fun launchDataLoad(block: suspend () -> Unit): Job
    {
        Log.d(TAG, "launchDataLoad starts with spinner value ${_spinner.value}")
        return viewModelScope.launch {
            try
            {
                _spinner.value = true
                Log.d(TAG, "launchDataLoad try with spinner value ${_spinner.value}")
                block()
            }
            catch (error: Throwable)
            {
                _toast.value = error.message
            }
            finally
            {
                _spinner.value = false

                Log.d(TAG, "launchDataLoad ends with spinner value ${_spinner.value}")
            }
        }
    }



    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, activity: Activity)
    {
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task, activity)
        }
    }


    override fun onCleared() {
        Log.d(TAG, "onCleared starts")
        super.onCleared()
        Log.d(TAG, "onCleared ends")
    }

}