package com.example.stayhealthy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhealthy.R
import com.example.stayhealthy.data.PrefsHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import java.util.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val prefsHelper: PrefsHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //hide Title bar
        supportActionBar?.hide()

        prefsHelper.saveSelectedMealPlanDate(GregorianCalendar(Locale.getDefault()).timeInMillis)

        btnSignUp?.setOnClickListener {
            val registerActivity = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(registerActivity)
        }

        btnSignIn?.setOnClickListener {
            val loginActivity = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(loginActivity)
        }

        Log.d(TAG, "onCreate: ends")
    }
}