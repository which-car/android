package com.mahmutalperenunal.whichcar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mahmutalperenunal.whichcar.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controlUserState()


        //go to profileActivity
        binding.homeProfileIcon.setOnClickListener {
            val intentProfile = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intentProfile)
            finish()
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }


    //hide profile button if guest login
    private fun controlUserState() {
        if (intent.getStringExtra("user type") == "Guest") {
            binding.homeProfileIcon.visibility = View.GONE
        } else {
            binding.homeProfileIcon.visibility = View.VISIBLE
        }
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