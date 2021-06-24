package com.example.stayhealthy.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhealthy.R
import com.example.stayhealthy.data.PrefsHelper
import com.example.stayhealthy.data.models.domain.User
import com.example.stayhealthy.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_parameter.*
import kotlinx.android.synthetic.main.content_parameter.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.util.*

private const val TAG = "ParameterActivity"

class ParameterActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by inject()
    private var id: String? = null

    private val prefsHelper: PrefsHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameter)
        setSupportActionBar(toolbar)

        userViewModel.currentUserLD.observe(this, { user ->
            Log.d(TAG, "onCreate: observing user with $user")
            etName.setText(user?.name)
            id = user?.id
            user?.id?.let { prefsHelper.saveUserFirstLogin(it, true) }
        })

        setActivityLevelAdapter()
        addListenerOnButton()
        Log.d(TAG, "onCreate: ends")
    }

    private fun setActivityLevelAdapter() {
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
                this,
                R.array.activity_level_arrays,
                R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerActivityLevel.adapter = adapter

        // so you can also click on arrow to open spinner
        ivSpinnerArrow.setOnClickListener { spinnerActivityLevel.performClick() }
    }

    private fun addListenerOnButton() {

        btnConfirmData?.setOnClickListener {
            if (!parameterFormIsValid()) {
                Toast.makeText(
                        this@ParameterActivity,
                        getString(R.string.fill_all_parameters),
                        Toast.LENGTH_SHORT
                )
                        .show()
            } else {

                val mGender = checkSex()

                id?.let { id ->
                    val user = User(
                            id,
                            etName.text.toString(),
                            mGender,
                            etAge.text.toString().toLong(),
                            etHeight.text.toString().toLong(),
                            etWeight.text.toString().toLong(),
                            spinnerActivityLevel.selectedItem.toString()
                    )

                    userViewModel.updateUserInFirestore(
                            user,
                            this
                    ) // userViewModel is singleton / coroutines inside won't be cancelled until onCleared() is called
                }
                startActivity(HomeActivity())
            }
        }
    }

    private fun parameterFormIsValid(): Boolean {
        return etName.text.isNotEmpty() &&
                etAge.text.isNotEmpty() &&
                etHeight.text.isNotEmpty() &&
                etWeight.text.isNotEmpty()
    }

    private fun startActivity(activity: Activity) {
        Log.d(TAG, "startActivity: starting activity $activity")
        val intent = Intent(this@ParameterActivity, activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun checkSex(): String {
        Log.d(TAG, "checkSex: starting")
        return if (rbMale.isChecked) {
            getString(R.string.sex_male)
        } else {
            getString(R.string.sex_female)
        }
    }

    fun onClickCheckSex(view: View) {
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.rbMale -> {
            }
            R.id.rbFemale -> {
            }
        }
    }

}