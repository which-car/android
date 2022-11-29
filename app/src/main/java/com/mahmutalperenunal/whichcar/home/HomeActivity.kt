package com.mahmutalperenunal.whichcar.home

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.mahmutalperenunal.whichcar.profile.ProfileActivity
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.cardetail.CarsActivity
import com.mahmutalperenunal.whichcar.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var animationFromBottom: Animation

    private lateinit var sharedPreferencesUsernamePassword: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set sharedPreferences
        sharedPreferencesUsernamePassword = getSharedPreferences("autoUsernamePassword", MODE_PRIVATE)

        getUsername()

        //set animation
        animationFromBottom = AnimationUtils.loadAnimation(this, R.anim.home_from_bottom)

        //home page starting animation
        binding.homeSplashImageView.animate().translationY(-1200F).setDuration(800).startDelay = 300
        binding.homeSplashLinearLayout.animate().translationY(140F).alpha(0F).setDuration(800).startDelay = 300
        binding.homeTitleLinearLayout.startAnimation(animationFromBottom)
        binding.homeMenuLinearLayout.startAnimation(animationFromBottom)


        //go to carsActivity
        binding.homeCarDetailButton.setOnClickListener {
            val intentCars = Intent(applicationContext, CarsActivity::class.java)
            startActivity(intentCars)
            finish()
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //go to profileActivity
        binding.homeProfileButton.setOnClickListener { openProfile() }

        //open about alertDialog
        binding.homeAboutButton.setOnClickListener { about() }
    }


    //get username
    private fun getUsername() {
        if (sharedPreferencesUsernamePassword.getString("username", null) != "") {
            binding.homeUsernameTextView.text = sharedPreferencesUsernamePassword.getString("username", null)
        } else {
            binding.homeUsernameTextView.text = ""
        }
    }


    //alert profile button if guest login
    private fun openProfile() {

        if (intent.getStringExtra("user type") == "Guest") {

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Profil")
                .setMessage("Kullanıcı girişi yapmalısınız!")
                .setIcon(R.drawable.wrong)
                .setNegativeButton("Tamam") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

        } else {

            val intentProfile = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intentProfile)
            finish()
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

    }


    //about the app
    private fun about() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Hakkında")
            .setMessage("Bu uygulama ile araç önerisi alabilir, teknik detaylarını görüntüleyebilir ve yorumda bulunabilirsiniz.")
            .setIcon(R.drawable.question)
            .setPositiveButton("Tamam") {
                    dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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