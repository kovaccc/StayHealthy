package com.example.stayhealthy.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhealthy.R
import com.example.stayhealthy.module.DATE_MEAL_PLAN
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.util.*


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {


    private val dateSharedPreferences : SharedPreferences by inject(named("datePrefs"))

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //hide Title bar
        supportActionBar?.hide()

        dateSharedPreferences.edit().putLong(DATE_MEAL_PLAN, GregorianCalendar(Locale.getDefault()).timeInMillis).apply() // put current date every time log out or login/register

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