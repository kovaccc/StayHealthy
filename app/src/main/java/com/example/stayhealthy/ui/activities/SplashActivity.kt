package com.example.stayhealthy.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stayhealthy.R
import android.app.Activity
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import org.koin.android.ext.android.inject
import com.example.stayhealthy.data.models.domain.User
import com.example.stayhealthy.viewmodels.UserViewModel
import kotlinx.coroutines.*
import java.util.*


private const val TAG = "SplashActivity"

private const val COROUTINE_STATE = "CoroutineState"

class SplashActivity : AppCompatActivity() {
    private val userViewModel: UserViewModel by inject()

    private var user: User? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var coroutineState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()


        userViewModel.currentUserLD.observe(this, {
            Log.d(TAG, "onCreate: observing user with value $it")
            user = it
        })

//        userViewModel.logOutUser() //TODO delete this after testing

        if (savedInstanceState != null) { // checking if rotation happened to prevent creating next activity few times if it gets in coroutine

            coroutineState = savedInstanceState.getBoolean(COROUTINE_STATE)

            Log.d(TAG, "onCreate: value of coroutine state is $coroutineState ")
        }

        if (!coroutineState) {
            coroutineScope.launch { // stays alive after rotation

                Log.d(TAG, "onCreate: coroutine launched with value of state  $coroutineState")
                coroutineState = true
                delay(3_000)
                val currentFirebaseUser = userViewModel.checkUserLoggedIn()

                if (currentFirebaseUser == null) {
                    Log.d(TAG, "onCreate: no user logged in")
                    startActivity(MainActivity())
                } else {
                    currentFirebaseUser.let { firebaseUser ->
                        Log.d(TAG, "ID of logged user is ${firebaseUser.uid} ")
                        userViewModel.getUserFromFirestore( // fill current user data in UserViewModel
                                firebaseUser.uid,
                                this@SplashActivity)

                        if (userViewModel.checkParametersFulfillment()) {
                            startActivity(HomeActivity())
                        } else {
                            startActivity(ParameterActivity())
                        }

                    }
                }
            }
        }
        Log.d(TAG, "onCreate: ends")
    }

    private fun startActivity(activity: Activity) {
        Log.d(TAG, "startActivity: starting activity $activity")
        val intent = Intent(this@SplashActivity, activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    // print key hash for facebook login
//    private fun printKeyHash()
//    {
//        try
//        {
//            val info = packageManager.getPackageInfo(applicationContext.packageName, PackageManager.GET_SIGNATURES)
//            for(signature in info.signatures)
//            {
//                val messageDigest = MessageDigest.getInstance("SHA")
//                messageDigest.update(signature.toByteArray())
//                Log.i("printKeyHash_1", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT))
//            }
//        }
//        catch (exception: PackageManager.NameNotFoundException)
//        {
//            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
//        }
//        catch (exception: NoSuchAlgorithmException)
//        {
//            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Log.d(TAG, "onSaveInstanceState starts")
        super.onSaveInstanceState(outState, outPersistentState)
        Log.d(TAG, "onSaveInstanceState ends")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(COROUTINE_STATE, coroutineState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstanceState starts")
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState ends")
    }

}