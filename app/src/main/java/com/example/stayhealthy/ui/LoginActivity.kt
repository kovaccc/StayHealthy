package com.example.stayhealthy.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhealthy.R
import com.example.stayhealthy.dialogs.ForgotPasswordDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject


private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() { // only portrait mode is allowed

    private val userViewModel: UserViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        userViewModel.toast.observe(this, { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                userViewModel.onToastShown()
            }
        })


        userViewModel.spinner.observe(this, { spinner ->
            spinner?.let {it ->
                if (it) {
                    spinner_login.visibility = View.VISIBLE

                } else {
                    spinner_login.visibility = View.GONE
                }
                Log.i(TAG, "$it")
            }
        })


        btn_login_login.setOnClickListener {

                if (validateEmail(tiet_login_email) && validatePassword()) {
                    userViewModel.logInUserFromAuthWithEmailAndPassword(
                        tiet_login_email.text.toString(),
                        tiet_login_password.text.toString(),
                        this@LoginActivity
                    )
                }
        }

        iv_login_facebook.setOnClickListener {
                userViewModel.signInWithFacebook(this@LoginActivity)
        }

        iv_login_google.setOnClickListener {
                userViewModel.signInWithGoogle(this@LoginActivity)
        }


        tv_login_registernow.setOnClickListener {
            startRegisterActivity()
        }

        tv_login_forgotPassword.setOnClickListener {
            val dialog = ForgotPasswordDialog()
            dialog.show(supportFragmentManager, "forgot_password")
        }

        Log.d(TAG, "onCreate: ends")
    }

    private fun startRegisterActivity()
    {
        Log.d(TAG, "startRegisterActivity: starts")
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish() // so in register activity you go with back button to MainActivity
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
    private fun validatePassword(): Boolean
    {
        val password = tiet_login_password.text.toString().trim()

        return if(password.length < 6)
        {
            tiet_login_password.error = "Use at least 6 characters"
            false
        }
        else
        {
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        Log.d(TAG, "onActivityResult: starts")
        super.onActivityResult(requestCode, resultCode, data)
        userViewModel.onActivityResult(requestCode, resultCode, data, this@LoginActivity)

        Log.d(TAG, "onActivityResult: ends")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: starts")
    }

}