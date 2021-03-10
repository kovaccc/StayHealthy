package com.example.stayhealthy.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhealthy.R
import com.example.stayhealthy.model.User
import com.example.stayhealthy.module.APP_START_FIRST_LOGIN

import kotlinx.android.synthetic.main.activity_parameter.*
import kotlinx.android.synthetic.main.content_parameter.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlinx.android.synthetic.main.layout_loading_dialog.*
import org.koin.core.qualifier.named


private const val TAG = "ParameterActivity"

class ParameterActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by inject()
    private var id: String? = null

    private val appStartSharedPreferences : SharedPreferences by inject(named("appStartPrefs"))

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameter)
        setSupportActionBar(toolbar)

        appStartSharedPreferences.edit().putBoolean(APP_START_FIRST_LOGIN, true).apply()

        userViewModel.currentUserLD.observe(this, { user ->
            Log.d(TAG, "onCreate: observing user with $user")
            etName.setText(user?.name)
            id = user?.id
        })

        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(this, R.array.activity_level_arrays, R.layout.spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerActivityLevel.adapter = adapter

        addListenerOnButton()
        Log.d(TAG, "onCreate: ends")

    }

    private fun addListenerOnButton() {

        // so you can also click on arrow to open spinner
        ivSpinnerArrow.setOnClickListener { spinnerActivityLevel.performClick() }

        btnConfirmData?.setOnClickListener {
            if (etName.text.isEmpty() || etAge.text.isEmpty()|| etHeight.text.isEmpty() || etWeight.text.isEmpty()) {
                Toast.makeText(this@ParameterActivity, "Fill all parameters!", Toast.LENGTH_SHORT).show()
            } else {

                val mGender = checkSex()

                val user = User(id!!, etName.text.toString(), mGender, etAge.text.toString().toLong(),
                            etHeight.text.toString().toLong(), etWeight.text.toString().toLong(), spinnerActivityLevel.selectedItem.toString())

                userViewModel.updateUserInFirestore(user, this) // userViewModel is singleton / coroutines inside won't be cancelled until onCleared() is called
                startActivity(HomeActivity())
            }
        }
    }

    private fun startActivity(activity: Activity)
    {
        Log.d(TAG, "startActivity: starting activity $activity")
        val intent = Intent(this@ParameterActivity, activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun checkSex() : String {
        Log.d(TAG, "checkSex: starting")
        return if(rbMale.isChecked) {
            getString(R.string.sex_male)
        } else {
            getString(R.string.sex_female)
        }
    }

    fun onClickCheckSex(view: View) { // not used
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.rbMale -> {
            }
            R.id.rbFemale -> {
            }
        }
    }

}