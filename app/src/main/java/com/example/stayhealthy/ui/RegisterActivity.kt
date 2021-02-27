package com.example.stayhealthy.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhealthy.R
import kotlinx.android.synthetic.main.activity_register.*
import android.widget.Toast
import android.util.Log
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject



private const val TAG = "RegisterActivity"
class RegisterActivity : AppCompatActivity() { // only portrait mode is allowed in AndroidManifest.xml
    private val userViewModel: UserViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        printKeyHash()

        userViewModel.toast.observe(this, { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                userViewModel.onToastShown()
            }
        })

        userViewModel.spinner.observe(this, { value ->
            value.let {show ->
                if (show) {
                   spinner_register.visibility = View.VISIBLE

                } else spinner_register.visibility = View.GONE
                Log.i(TAG, "$show")
            }
        })


        btn_signup_signup.setOnClickListener {

                if (validateName() && validateEmail() && validatePassword()) {
                     userViewModel.registerUserFromAuthWithEmailAndPassword(
                            tiet_signup_name.text.toString(),
                            tiet_singup_email.text.toString(),
                            tiet_signup_password.text.toString(),
                            this@RegisterActivity
                    )
            }
        }



        iv_register_facebook.setOnClickListener {
                userViewModel.signInWithFacebook(this@RegisterActivity)

        }

        iv_register_google.setOnClickListener {
                userViewModel.signInWithGoogle(this@RegisterActivity)

        }


        tvHaveAccount.setOnClickListener {
            startLoginActivity()
        }

    }

    // key hash for facebook authentication
//    private fun printKeyHash()
//    {
//        try
//        {
//            val info = packageManager.getPackageInfo(applicationContext.packageName, PackageManager.GET_SIGNATURES)
//            for(signature in info.signatures)
//            {
//                val messageDigest = MessageDigest.getInstance("SHA")
//                messageDigest.update(signature.toByteArray())
//                Log.i("printKeyHash_2", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT))
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

    private fun startLoginActivity()
    {
        Log.d(TAG, "startLoginActivity: starts")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateName(): Boolean
    {
        val name = tiet_signup_name.text.toString().trim()

        return if(name.length < 6)
        {
            tiet_signup_name.error = "Use at least 6 characters"
            false
        }
        else
        {
            true
        }
    }

    private fun validateEmail(): Boolean
    {
        val email = tiet_singup_email.text.toString().trim()

        return if(!email.contains("@") && !email.contains("."))
        {
            tiet_singup_email.error = "Enter a valid email"
            false
        }
        else if (email.length < 6)
        {
            tiet_singup_email.error = "Use at least 6 characters"
            false
        }
        else
        {
            true
        }
    }

    private fun validatePassword(): Boolean
    {
        val password = tiet_signup_password.text.toString().trim()

        return if(password.length < 6)
        {
            tiet_signup_password.error = "Use at least 5 characters"
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
        userViewModel.onActivityResult(requestCode, resultCode, data, this@RegisterActivity)
        Log.d(TAG, "onActivityResult: ends")
    }

}