package com.mahmutalperenunal.whichcar.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
        progressDialog.setMessage("Çıkış Yapılıyor...")


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

        //logout
        binding.profileLogoutButton.setOnClickListener { logOutDialog() }

        //back to homeActivity
        binding.profileBackButton.setOnClickListener { onBackPressed() }
    }


    //set app theme
    private fun setTheme() {

        androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Uygulama Teması")
            .setMessage("Uygulama temasını seçiniz.\n\nMevcut tema: $themeName")
            .setIcon(R.drawable.day_night)
            .setPositiveButton(
                "Açık"
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                editorTheme.putInt("theme", 1)
                editorTheme.apply()
            }
            .setNegativeButton(
                "Koyu"
            ) { _: DialogInterface?, _: Int ->
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                editorTheme.putInt("theme", 2)
                editorTheme.apply()
            }
            .setNeutralButton(
                "Sistem Teması"
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
                    Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()

                }
            })

        }


    //show logout dialog
    private fun logOutDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Çıkış Yap")
            .setMessage("Çıkış yapmak istediğinizden emin misiniz?")
            .setIcon(R.drawable.question)
            .setPositiveButton("Çıkış Yap") {
                    dialog, _ ->
                logoutProcess()
                dialog.dismiss()
            }
            .setNegativeButton("İptal") {
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

                Toast.makeText(applicationContext, "Çıkış Yapıldı!", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<Logout>, t: Throwable) {

                if (progressDialog.isShowing) progressDialog.dismiss()

                Log.e("Logout Error", t.printStackTrace().toString())

                Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()

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