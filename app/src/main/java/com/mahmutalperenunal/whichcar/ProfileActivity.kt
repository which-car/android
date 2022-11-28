package com.mahmutalperenunal.whichcar

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.mahmutalperenunal.whichcar.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private var username: String? = null
    private var email: String? = null
    private var userId: Int? = null
    private var userToken: String? = null
    private var theme: Int? = null

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
        theme = sharedPreferencesTheme.getInt("theme", 0)


        //set username
        binding.profileUserNameTextView.text = username


        checkTheme()

        changeTheme()

        //checkConnection()

        //getUserInformation()


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
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //back to homeActivity
        binding.profileBackButton.setOnClickListener { onBackPressed() }
    }


    //check connection
    /*private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (!isConnected) {
                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("İnternet Bağlantısı Yok")
                    .setMessage("Lütfen internet bağlantınızı kontrol edin!")
                    //.setIcon(R.drawable.without_internet)
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


    //check theme and select current theme button
    private fun checkTheme() {
        when(theme) {
            -1 -> binding.profileThemeButtonToggleGroup.check(R.id.profile_theme_systemDefault_button)
            2 -> binding.profileThemeButtonToggleGroup.check(R.id.profile_theme_dark_button)
            1 -> binding.profileThemeButtonToggleGroup.check(R.id.profile_theme_light_button)
        }
    }


    //set app theme
    private fun changeTheme() {
        binding.profileThemeButtonToggleGroup.addOnButtonCheckedListener { _, selectedBtnId, isChecked ->
            if (isChecked) {
                val theme = when (selectedBtnId) {
                    R.id.profile_theme_systemDefault_button -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM //-1
                    R.id.profile_theme_dark_button -> AppCompatDelegate.MODE_NIGHT_YES //2
                    else -> AppCompatDelegate.MODE_NIGHT_NO //1
                }
                Log.d("ProfileActivity", "theme:$theme")
                AppCompatDelegate.setDefaultNightMode(theme)
                editorTheme.putInt("theme", theme)
                editorTheme.apply()
            }
        }
    }


    /*private fun getUserInformation() {
        val repository = RepositoryUsers()
        val mainViewModelFactory = MainViewModelFactoryUsers(repository)
        mainViewModelUsers = ViewModelProvider(this, mainViewModelFactory)[MainViewModelUsers::class.java]
        mainViewModelUsers.getUsersId("Token $userToken", userId!!)
        mainViewModelUsers.getUsersIdRepository.observe(this) { response ->
            if (response.isSuccessful) {
                binding.profileProgressBar.visibility = View.GONE
                response.body()?.let {
                    val size = response.body()!!.size - 1
                    for (item in 0..size) {
                        response.body()!![item].photo = "https://kresapp.herokuapp.com" + response.body()!![item].photo
                        Log.d("URL", response.body()!![item].photo.toString())
                        Glide.with(applicationContext)
                            .load(response.body()!![item].photo)
                            .centerCrop()
                            .into(binding.profileProfilePhotoImageView)
                        email = response.body()!![item].email
                        binding.profileEmailTextView.text = response.body()!![item].email
                    }
                }
            } else {
                Log.e("Profile Error", response.code().toString())
                Toast.makeText(applicationContext, "İşlem başarısız!", Toast.LENGTH_SHORT).show()
            }
        }
    }*/


    //show logout dialog
    private fun logOutDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Çıkış Yap")
            .setMessage("Çıkıl yapmak istediğinizden emin misiniz?")
            //.setIcon(R.drawable.question)
            .setPositiveButton("Çıkış Yap") {
                    dialog, _ ->
                //logOutProcess()
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
    /*private fun logOutProcess() {

        progressDialog.show()

        val repository = RepositoryLogOut()
        val mainViewModelFactory = MainViewModelFactoryLogOut(repository)
        mainViewModelLogOut = ViewModelProvider(this, mainViewModelFactory)[MainViewModelLogOut::class.java]
        mainViewModelLogOut.postLogOutRequest()
        mainViewModelLogOut.postLogOutRequestRepository.observe(this) { response ->
            if (response.isSuccessful) {
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

                onBackPressed()

                Toast.makeText(applicationContext, "Çıkış Yapıldı!", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("Logout Error", response.code().toString())
                Toast.makeText(applicationContext, "İşlem Başarısız. Lütfen Tekrar Deneyin!", Toast.LENGTH_SHORT).show()
            }
        }

    }*/


    //back to homeActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}