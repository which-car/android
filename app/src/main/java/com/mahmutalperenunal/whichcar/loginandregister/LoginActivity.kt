package com.mahmutalperenunal.whichcar.loginandregister

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.databinding.ActivityLoginBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity

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
        progressDialog.setMessage("Giriş Yapılıyor...")


        passwordEdittext = binding.loginPasswordEditText


        //set sharedPreferences
        sharedPreferencesAutoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE)
        sharedPreferencesUsernamePassword = getSharedPreferences("autoUsernamePassword", MODE_PRIVATE)
        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)

        editorAutoLogin = sharedPreferencesAutoLogin.edit()
        editorUsername = sharedPreferencesUsernamePassword.edit()
        editorAuthToken = sharedPreferencesAuthToken.edit()


        //checkConnection()
        //checkLoginState()


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
                    binding.loginUsernameEditText.error = "Bu alan boş bırakılamaz!"
                }
                binding.loginPasswordEditText.text.toString().isEmpty() -> {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    binding.loginPasswordEditText.error = "Bu alan boş bırakılamaz!"
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
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }


        //registerActivity
        binding.loginRegisterButton.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        //forgot password
        binding.loginForgotPasswordTextView.setOnClickListener {

            if (progressDialog.isShowing) progressDialog.dismiss()

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Şifreni mi unuttun?")
                .setMessage("Şifrenizi öğrenmek için lütfen iletişime geçiniz.")
                .setIcon(R.drawable.question)
                .setNegativeButton("Tamam") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

        }
    }


    //check network connection
    /*private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected) {
                connection = true
            } else {
                connection = false
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

    }*/


    //auto login
    /*private fun autoLogin() {
        editorAutoLogin.putString("remember", "true")
        editorAutoLogin.apply()
    }*/


    //check auto login state
    /*private fun checkLoginState() {
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

    }*/


    /*private fun startHomeActivity() {

        binding.loginLoginButton.setOnClickListener {

            intent = Intent(applicationContext, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            //if username and password edittext is empty, set error
            when {
                binding.loginUsernameEditTextLayout.isEmpty() -> {
                    binding.loginUsernameEditTextLayout.error = "Bu alan boş bırakılamaz!"
                }
                binding.loginPasswordEditText.text.toString().isEmpty() -> {
                    binding.loginPasswordEditText.error = "Bu alan boş bırakılamaz!"
                }

                //if username and password edittext not empty, login authentication request and start homeActivity
                else -> {
                    loginProcess()
                }
            }

        }

    }*/


    //login process
    /*private fun loginProcess() {

        progressDialog.show()

        val intentHome = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        val username = binding.loginUsernameEditText.text.toString().trim()
        val password = binding.loginPasswordEditText.text.toString().trim()

        val repository = RepositoryLogIn()
        val mainViewModelFactory = MainViewModelFactoryLogIn(repository)
        mainViewModelLogIn = ViewModelProvider(this, mainViewModelFactory)[MainViewModelLogIn::class.java]
        val postLogInRequest = LogInRequest(username, password)
        mainViewModelLogIn.postLogInRequest(postLogInRequest)
        mainViewModelLogIn.postLogInRequestRepository.observe(this) { response ->
            if (response.isSuccessful) {

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

                Toast.makeText(applicationContext, "Giriş Yapıldı!", Toast.LENGTH_SHORT).show()

            } else {
                if (progressDialog.isShowing) progressDialog.dismiss()

                Log.e("Login Error", response.code().toString())

                binding.loginUsernameEditTextLayout.error
                binding.loginPasswordEditTextLayout.error

                Toast.makeText(applicationContext, "Hatalı Giriş! Lütfen Tekrar Deneyin.", Toast.LENGTH_SHORT).show()
            }
        }

    }*/


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