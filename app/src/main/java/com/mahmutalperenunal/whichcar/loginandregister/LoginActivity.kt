package com.mahmutalperenunal.whichcar.loginandregister

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isEmpty
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityLoginBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import com.mahmutalperenunal.whichcar.model.auth.AuthToken
import com.mahmutalperenunal.whichcar.model.auth.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var progressDialog: ProgressDialog

    private lateinit var sharedPreferencesAutoLogin: SharedPreferences
    private lateinit var sharedPreferencesUsernamePassword: SharedPreferences
    private lateinit var sharedPreferencesAuthToken: SharedPreferences

    private lateinit var editorAutoLogin: SharedPreferences.Editor
    private lateinit var editorUsername: SharedPreferences.Editor
    private lateinit var editorAuthToken: SharedPreferences.Editor

    private var passwordEdittext: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //animation to show until username and password are verified
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(R.string.signing_in_text.toString())


        passwordEdittext = binding.loginPasswordEditText


        //set sharedPreferences
        sharedPreferencesAutoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE)
        sharedPreferencesUsernamePassword = getSharedPreferences("autoUsernamePassword", MODE_PRIVATE)
        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)

        editorAutoLogin = sharedPreferencesAutoLogin.edit()
        editorUsername = sharedPreferencesUsernamePassword.edit()
        editorAuthToken = sharedPreferencesAuthToken.edit()


        checkConnection()
        checkLoginState()


        //clear username edittext
        binding.loginUsernameEditTextLayout.setEndIconOnClickListener {
            binding.loginUsernameEditText.text!!.clear()
        }


        //homeActivity with username and password
        binding.loginLoginButton.setOnClickListener {

            progressDialog.show()

            //if username and password edittext is empty, set error
            when {
                binding.loginUsernameEditText.text.toString().isEmpty() -> {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    binding.loginUsernameEditText.error = resources.getString(R.string.not_left_blank_text)
                }
                binding.loginPasswordEditText.text.toString().isEmpty() -> {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    binding.loginPasswordEditText.error = resources.getString(R.string.not_left_blank_text)
                }

                //if username and password edittext not empty, login authentication request and start homeActivity
                else -> {
                    if (progressDialog.isShowing) progressDialog.dismiss()

                    editorUsername.putString("rememberUsername", "true")
                    editorUsername.putString("username", binding.loginUsernameEditText.text.toString().trim())
                    editorUsername.putString("password", binding.loginPasswordEditText.text.toString().trim())
                    editorUsername.apply()

                    val intent = Intent(applicationContext, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("user type", "Login")
                    startActivity(intent)
                    finish()

                    //loginProcess()
                }
            }

        }


        //homeActivity with continue as guest
        binding.loginContinueAsAGuestTextView.setOnClickListener {

            editorUsername.putString("rememberUsername", "false")
            editorUsername.putString("username", "")
            editorUsername.putString("password", "")
            editorUsername.apply()

            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("user type", "Guest")
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }


        //registerActivity
        binding.loginRegisterButton.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        //forgot password
        binding.loginForgotPasswordTextView.setOnClickListener {

            if (progressDialog.isShowing) progressDialog.dismiss()

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle(R.string.forgot_password_text)
                .setMessage(R.string.forgot_password_description_text)
                .setIcon(R.drawable.question)
                .setNegativeButton(R.string.ok_text) {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

        }
    }


    //check network connection
    private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (!isConnected) {

                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.no_internet_connection_title_text)
                    .setMessage(R.string.no_internet_connection_description_text)
                    .setIcon(R.drawable.without_internet)
                    .setNegativeButton(R.string.ok_text) {
                            dialog, _ ->
                        checkConnection()
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            }
        })

    }


    //auto login
    private fun autoLogin() {
        editorAutoLogin.putString("remember", "true")
        editorAutoLogin.apply()
    }


    //check auto login state
    private fun checkLoginState() {
        val checkData = sharedPreferencesAutoLogin.getString("remember", null)
        val checkUsername = sharedPreferencesUsernamePassword.getString("rememberUsername", null)

        val username = sharedPreferencesUsernamePassword.getString("username", null)

        if (checkData == "true") {
            val intent = Intent(applicationContext, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        } else {
            startHomeActivity()
        }

        if (checkUsername == "true") {
            binding.loginUsernameEditText.setText(username)
        }
        else {
            binding.loginUsernameEditText.setText("")
        }

    }


    private fun startHomeActivity() {

        binding.loginLoginButton.setOnClickListener {

            intent = Intent(applicationContext, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            //if username and password edittext is empty, set error
            when {
                binding.loginUsernameEditTextLayout.isEmpty() -> {
                    binding.loginUsernameEditTextLayout.error = resources.getString(R.string.not_left_blank_text)
                }
                binding.loginPasswordEditText.text.toString().isEmpty() -> {
                    binding.loginPasswordEditText.error = resources.getString(R.string.not_left_blank_text)
                }

                //if username and password edittext not empty, login authentication request and start homeActivity
                else -> {

                    val intent = Intent(applicationContext, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("user type", "Login")
                    startActivity(intent)
                    finish()

                    //loginProcess()

                }
            }

        }

    }


    //login process
    private fun loginProcess() {

        progressDialog.show()

        val intentHome = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val username = binding.loginUsernameEditText.text.toString().trim()
        val password = binding.loginPasswordEditText.text.toString().trim()

        val postLogin = Login(username, password)

        val retrofit = RetrofitInstance.apiLogin

        val call: Call<AuthToken> = retrofit.postLogin(postLogin)
        call.enqueue(object : Callback<AuthToken> {
            override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {

                val authToken = response.body()!!.authToken

                editorAuthToken.putString("token", authToken)
                editorAuthToken.apply()

                Log.d("Token", response.body()!!.authToken)

                //auto login if token accessed
                autoLogin()

                intentHome.putExtra("Username", username)

                //start homeActivity
                startActivity(intentHome)
                finish()

                if (progressDialog.isShowing) progressDialog.dismiss()

                Toast.makeText(applicationContext, R.string.logged_text, Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<AuthToken>, t: Throwable) {

                if (progressDialog.isShowing) progressDialog.dismiss()

                Log.e("Login Error", t.printStackTrace().toString())

                binding.loginUsernameEditTextLayout.error
                binding.loginPasswordEditTextLayout.error

                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

            }
        })

    }


    //exit application
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentMainExit = Intent(Intent.ACTION_MAIN)
        intentMainExit.addCategory(Intent.CATEGORY_HOME)
        intentMainExit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intentMainExit)
        finish()
    }
}