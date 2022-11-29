package com.mahmutalperenunal.whichcar.cardetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mahmutalperenunal.whichcar.databinding.ActivityCarsBinding

class CarsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}