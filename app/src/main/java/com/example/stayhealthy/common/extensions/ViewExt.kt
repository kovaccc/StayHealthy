package com.example.stayhealthy.common.extensions

import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.stayhealthy.R
import com.google.android.material.textfield.TextInputEditText

fun View.validateEmail() : Boolean {
    val email = (this as TextInputEditText).text.toString().trim()
    return if (!email.contains("@") && !email.contains(".")) {
        this.error = Resources.getSystem().getString(R.string.enter_valid_email)
        false
    } else if (email.length < 6) {
        this.error = Resources.getSystem().getString(R.string.use_at_least_6_characters)
        false
    } else {
        true
    }
}



fun View.validatePassword(): Boolean {
    val password = (this as TextInputEditText).text.toString().trim()

    return if (password.length < 6) {
        this.error = Resources.getSystem().getString(R.string.use_at_least_6_characters)
        false
    } else {
        true
    }
}

fun View.hideKeyboard() {
    val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

