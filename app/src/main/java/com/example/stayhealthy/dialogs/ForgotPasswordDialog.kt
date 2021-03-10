package com.example.stayhealthy.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.stayhealthy.R
import com.example.stayhealthy.ui.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import org.koin.android.ext.android.inject


private const val TAG = "ForgotPasswordDialog"
class ForgotPasswordDialog : AppCompatDialogFragment() {

    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ForgotPasswordDialogStyle)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: called")

        return inflater.inflate(R.layout.dialog_forgot_password, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")

        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btn_forgotpassword_send.setOnClickListener {
            if(validateEmail(tiet_forgotpassword_email))
            {
                userViewModel.sendPasswordResetEmail(
                        tiet_forgotpassword_email.text.toString(),
                        requireActivity()
                )
              dismiss()
            }
        }

        btn_forgotpassword_cancel.setOnClickListener {
            dismiss()
        }

    }

    private fun validateEmail(view: View): Boolean
    {
        val email = (view as TextInputEditText).text.toString().trim()

        return if(!email.contains("@") && !email.contains("."))
        {
            view.error = "Enter a valid email"
            false
        }
        else if (email.length < 6)
        {
            view.error = "Use at least 6 characters"
            false
        }
        else
        {
            true
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
    }

}