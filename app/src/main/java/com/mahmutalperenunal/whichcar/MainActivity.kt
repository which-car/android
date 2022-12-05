package com.mahmutalperenunal.whichcar

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityMainBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import com.mahmutalperenunal.whichcar.loginandregister.LoginActivity
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import com.mahmutalperenunal.whichcar.model.auth.AuthToken
import com.mahmutalperenunal.whichcar.model.auth.Login
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferencesAutoLogin: SharedPreferences
    private lateinit var sharedPreferencesUsernamePassword: SharedPreferences
    private lateinit var sharedPreferencesAuthToken: SharedPreferences
    private lateinit var sharedPreferencesTheme: SharedPreferences

    private lateinit var editorAutoLogin: SharedPreferences.Editor
    private lateinit var editorUsername: SharedPreferences.Editor
    private lateinit var editorAuthToken: SharedPreferences.Editor

    private var theme: Int? = null

    private var username: String = ""
    private var password: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set sharedPreferences
        sharedPreferencesAutoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE)
        sharedPreferencesUsernamePassword = getSharedPreferences("autoUsernamePassword", MODE_PRIVATE)
        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)

        username = sharedPreferencesUsernamePassword.getString("username", null).toString()
        password = sharedPreferencesUsernamePassword.getString("password", null).toString()

        editorAutoLogin = sharedPreferencesAutoLogin.edit()
        editorUsername = sharedPreferencesUsernamePassword.edit()
        editorAuthToken = sharedPreferencesAuthToken.edit()

        checkTheme()

        checkConnection()

        startLoginActivity()
    }


    //control app theme
    private fun checkTheme() {

        theme = sharedPreferencesTheme.getInt("theme", 0)

        val appTheme = when (theme) {
            -1 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM //-1
            2 -> AppCompatDelegate.MODE_NIGHT_YES //2
            else -> AppCompatDelegate.MODE_NIGHT_NO //1
        }
        Log.d("MainActivity", "theme:$appTheme")
        AppCompatDelegate.setDefaultNightMode(appTheme)

    }


    //check network connection
    private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected) {
                startLoginActivity()
            } else {
                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("İnternet Bağlantısı Yok")
                    .setMessage("Lütfen internet bağlantınızı kontrol edin!")
                    .setIcon(R.drawable.without_internet)
                    .setNegativeButton("Tamam") {
                            dialog, _ ->
                        checkConnection()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        })

    }


    //auto login process
    private fun autoLoginProcess() {

        if (sharedPreferencesAutoLogin.getString("remember", null) == "true") {

            val intentHome = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            val postLogin = Login(username, password)

            val retrofit = RetrofitInstance.apiLogin

            val call: Call<AuthToken> = retrofit.postLogin(postLogin)
            call.enqueue(object : Callback<AuthToken> {
                override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {

                    val authToken = response.body()!!.authToken

                    //save token
                    editorAuthToken.putString("token", authToken)
                    editorAuthToken.apply()

                    Log.d("Token", response.body()!!.authToken)

                    //auto login if token accessed
                    editorAutoLogin.putString("remember", "true")
                    editorAutoLogin.apply()

                    intentHome.putExtra("Username", username)

                    //start homeActivity
                    val startActivityTimer = timerTask {
                        startActivity(intentHome)
                    }

                    val timer = Timer()
                    timer.schedule(startActivityTimer, 2000)

                }

                override fun onFailure(call: Call<AuthToken>, t: Throwable) {

                    Log.e("Login Error", t.printStackTrace().toString())

                    val intentLogin = Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                    val startActivityTimer = timerTask {
                        startActivity(intentLogin)
                    }

                    val timer = Timer()
                    timer.schedule(startActivityTimer, 3000)

                }
            })

        } else {

            val intentLogin = Intent(applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            val startActivityTimer = timerTask {
                startActivity(intentLogin)
            }

            val timer = Timer()
            timer.schedule(startActivityTimer, 3000)

        }


    }


    private fun startLoginActivity() {
        val intentLogin = Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val startActivityTimer = timerTask {
            startActivity(intentLogin)
        }

        val timer = Timer()
        timer.schedule(startActivityTimer, 2000)
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