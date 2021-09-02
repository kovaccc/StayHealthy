package com.example.stayhealthy.util

import android.content.Context
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

object BindingAdapters {

    @BindingAdapter("unfocusAfterEnter")
    @JvmStatic
    fun unfocusOnEnterPressed(editText: AppCompatEditText, unfocus: Boolean?) {
        if (unfocus == true) {
            editText.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    editText.isFocusable = false
                    editText.isFocusableInTouchMode = true
                    val inputMethodManager =
                            editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
                    true
                } else {
                    false
                }
            }
        }
    }


    @BindingAdapter(
            "inputFieldFirstText",
            "inputFieldSecondText",
            "inputFieldThirdText",
            "inputFieldFirstError",
            "inputFieldSecondError",
            "inputFieldThirdError"
    )
    @JvmStatic
    fun validationForm(
            view: View,
            inputFieldFirstText: String?,
            inputFieldSecondText: String?,
            inputFieldThirdText: String?,
            inputFieldFirstError: Int?,
            inputFieldSecondError: Int?,
            inputFieldThirdError: Int?
    ) {
        view.isEnabled =
                !TextUtils.isEmpty(inputFieldFirstText) &&
                        !TextUtils.isEmpty(inputFieldSecondText) &&
                        !TextUtils.isEmpty(inputFieldThirdText) &&
                        inputFieldFirstError == null &&
                        inputFieldSecondError == null &&
                        inputFieldThirdError == null
    }


    @BindingAdapter("error")
    @JvmStatic
    fun setError(til: TextInputLayout, @StringRes error: Int?) {
        til.error = error?.let { til.context.getString(it) }
    }


    @BindingAdapter("onItemSelected")
    @JvmStatic
    fun setSpinnerItemsListener(
            spinner: AppCompatSpinner,
            onItemSelectedListener: AdapterView.OnItemSelectedListener?
    ) {
        onItemSelectedListener?.let { it ->
            spinner.onItemSelectedListener = it
        }
    }


}