package com.example.stayhealthy.data

import android.content.SharedPreferences
import com.example.stayhealthy.config.KEY_DATE_MEAL_PLAN
import com.example.stayhealthy.ui.dialogs.DATE_DEFAULT

class PrefsHelper(private val dateSharedPreferences: SharedPreferences, private val appSharedPreferences: SharedPreferences) {

    fun saveSelectedMealPlanDate(dateInMillis: Long) {
        dateSharedPreferences.edit().putLong(KEY_DATE_MEAL_PLAN, dateInMillis).apply()
    }

    fun getSelectedMealPlanDate(): Long {
        return dateSharedPreferences.getLong(KEY_DATE_MEAL_PLAN, DATE_DEFAULT)
    }

    fun saveUserFirstLogin(userId: String, value: Boolean) {
        appSharedPreferences.edit().putBoolean(userId, value).apply()
    }

    fun getUserFirstLogin(userId: String): Boolean {
        return appSharedPreferences.getBoolean(userId, false)
    }
}