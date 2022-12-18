package com.mahmutalperenunal.whichcar.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityProfileBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import com.mahmutalperenunal.whichcar.loginandregister.LoginActivity
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import com.mahmutalperenunal.whichcar.model.User
import com.mahmutalperenunal.whichcar.model.auth.Logout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private var username: String? = null
    private var email: String? = null
    private var userId: Int? = null
    private var userToken: String? = null

    private var themeCode: Int = 0
    private var themeName: String = ""

    private lateinit var sharedPreferencesAutoLogin: SharedPreferences
    private lateinit var sharedPreferencesUsernamePassword: SharedPreferences
    private lateinit var sharedPreferencesUserId: SharedPreferences
    private lateinit var sharedPreferencesAuthToken: SharedPreferences
    private lateinit var sharedPreferencesTheme: SharedPreferences

    private lateinit var editorTheme: SharedPreferences.Editor

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set sharedPreferences
        sharedPreferencesAutoLogin = getSharedPreferences("autoLogin", MODE_PRIVATE)
        sharedPreferencesUsernamePassword = getSharedPreferences("autoUsernamePassword", MODE_PRIVATE)
        sharedPreferencesUserId = getSharedPreferences("userId", MODE_PRIVATE)
        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)
        sharedPreferencesTheme = getSharedPreferences("appTheme", MODE_PRIVATE)

        editorTheme = sharedPreferencesTheme.edit()

        //get values
        username = sharedPreferencesUsernamePassword.getString("username", null)
        userId = sharedPreferencesUserId.getInt("id", 0)
        userToken = sharedPreferencesAuthToken.getString("token", null)


        //set username
        binding.profileUsernameTextView.text = username


        //check last theme
        checkLastTheme()

        checkConnection()

        getUserInformation()


        //set progressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(R.string.existing_text.toString())


        //go to profileEditActivity
        binding.profileEditButton.setOnClickListener {
            val intentEdit = Intent(applicationContext, ProfileEditActivity::class.java)
            intentEdit.putExtra("username", username)
            intentEdit.putExtra("email", email)
            startActivity(intentEdit)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //go to favouritesActivity
        binding.profileFavouritesButton.setOnClickListener {
            val intentFavorites = Intent(applicationContext, FavoritesActivity::class.java)
            startActivity(intentFavorites)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //change theme
        binding.profileThemeButton.setOnClickListener { setTheme() }

        //change language
        binding.profileChangeLanguageButton.setOnClickListener { changeLanguage() }

        //logout
        binding.profileLogoutButton.setOnClickListener { logOutDialog() }

        //back to homeActivity
        binding.profileBackButton.setOnClickListener { onBackPressed() }
    }


    //change app language
    private fun changeLanguage() {

        val languages = arrayOf(resources.getString(R.string.english_text), resources.getString(R.string.turkish_text))

        androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.app_language_text)
            .setIcon(R.drawable.language)
            .setSingleChoiceItems(languages, -1) { dialog, which ->
                when (which) {
                    0 -> {
                        setLocale("en")
                        recreate()
                    }
                    1 -> {
                        setLocale("tr")
                        recreate()
                    }
                }
                dialog.dismiss()
            }
            .setNeutralButton(R.string.cancel_text) {
                dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()

    }

    private fun setLocale(lang: String) {

        val locale = Locale(lang)
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)

        val editor: SharedPreferences.Editor= getSharedPreferences("Language", Context.MODE_PRIVATE).edit()
        editor.putString("app language", lang)
        editor.apply()
    }


    //set app theme
    private fun setTheme() {

        androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.app_theme_title_text)
            .setMessage(resources.getString(R.string.app_theme_description_text) + "\n\n${resources.getString(R.string.app_theme_description_text2)} $themeName")
            .setIcon(R.drawable.day_night)
            .setPositiveButton(
                R.string.light_text
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                editorTheme.putInt("theme", 1)
                editorTheme.apply()
            }
            .setNegativeButton(
                R.string.dark_text
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                editorTheme.putInt("theme", 2)
                editorTheme.apply()
            }
            .setNeutralButton(
                R.string.system_default_text
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
                editorTheme.putInt("theme", -1)
                editorTheme.apply()
            }
            .show()

    }


    //check last theme
    private fun checkLastTheme() {
        themeCode = sharedPreferencesTheme.getInt("theme", 0)

        when (themeCode) {
            -1 -> themeName = "Sistem Teması"
            1 -> themeName = "Açık"
            2 -> themeName = "Koyu"
        }
    }


    //check connection
    private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (!isConnected) {
                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.no_internet_connection_title_text)
                    .setMessage(resources.getString(R.string.no_internet_connection_description_text))
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


        //get user data
        private fun getUserInformation() {

            val retrofit = RetrofitInstance.apiUser

            val call: Call<User> = retrofit.getUser("Token $userToken", userId!!)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {

                    /*response.body()!!.profilePhoto = "https://carsuggestion.herokuapp.com" + response.body()!!.profilePhoto

                    Log.d("Profile URL", response.body()!!.profilePhoto.toString())

                    Glide.with(applicationContext)
                        .load(response.body()!!.profilePhoto)
                        .centerCrop()
                        .into(binding.profileProfilePhotoImageView)

                    email = response.body()!!.email

                    binding.profileEmailTextView.text = response.body()!!.email*/

                }

                override fun onFailure(call: Call<User>, t: Throwable) {

                    Log.e("Profile Error", t.printStackTrace().toString())
                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

                }
            })

        }


    //show logout dialog
    private fun logOutDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(R.string.logout_text)
            .setMessage(R.string.logout_description_text)
            .setIcon(R.drawable.question)
            .setPositiveButton(R.string.logout_text) {
                    dialog, _ ->
                logoutProcess()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel_text) {
                    dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    //logout process
    private fun logoutProcess() {

        progressDialog.show()

        val retrofit = RetrofitInstance.apiLogout

        val call: Call<Logout> = retrofit.postLogout()
        call.enqueue(object : Callback<Logout> {
            override fun onResponse(call: Call<Logout>, response: Response<Logout>) {

                Log.d("Logout Info", response.body()!!.detail)

                //clear auto login shared preferences data
                val editor: SharedPreferences.Editor = sharedPreferencesAutoLogin.edit()
                editor.clear()
                editor.apply()

                //clear auth token data
                val editorAuthToken: SharedPreferences.Editor = sharedPreferencesAuthToken.edit()
                editorAuthToken.clear()
                editorAuthToken.apply()

                //clear user id data
                val editorUserId: SharedPreferences.Editor = sharedPreferencesUserId.edit()
                editorUserId.clear()
                editorUserId.apply()

                if (progressDialog.isShowing) progressDialog.dismiss()

                //start loginActivity
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                Toast.makeText(applicationContext, R.string.logged_out_text, Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<Logout>, t: Throwable) {

                if (progressDialog.isShowing) progressDialog.dismiss()

                Log.e("Logout Error", t.printStackTrace().toString())

                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

            }
        })

    }


    //back to homeActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}