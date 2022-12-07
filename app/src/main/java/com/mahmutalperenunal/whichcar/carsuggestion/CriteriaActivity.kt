package com.mahmutalperenunal.whichcar.carsuggestion

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityCriteriaBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import com.mahmutalperenunal.whichcar.model.CarSuggestion
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CriteriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriteriaBinding

    private var baggageSize: String = ""
    private var fuelEfficiency: String = ""
    private var performance: String = ""
    private var safety: String = ""
    private var gearbox: String = ""
    private var chassisType: String = ""
    private var wheelDrive: String = ""
    private var minPrice: String = ""
    private var maxPrice: String = ""

    private lateinit var progressDialog: ProgressDialog

    private var userToken: String? = null

    private lateinit var sharedPreferencesAuthToken: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriteriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //animation to show until car suggested
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Öneriler Hazırlanıyor...")


        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)
        userToken = sharedPreferencesAuthToken.getString("token", null)


        setSelectableData()

        checkConnection()


        //go to suggestedCarActivity
        binding.criteriaSuggestButton.setOnClickListener { postCriteriaData() }

        //back to criteriaActivity
        binding.criteriaBackButton.setOnClickListener { onBackPressed() }
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


    //set selectable data
    private fun setSelectableData() {

        val baggageSizeList = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val adapterBaggageSizeList = ArrayAdapter(this, R.layout.selectable_criteria_list, baggageSizeList)
        binding.criteriaBaggageSizeEditText.setAdapter(adapterBaggageSizeList)

        val fuelEfficiencyList = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val adapterFuelEfficiencyList = ArrayAdapter(this, R.layout.selectable_criteria_list, fuelEfficiencyList)
        binding.criteriaFuelEfficiencyEditText.setAdapter(adapterFuelEfficiencyList)

        val performanceList = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val adapterPerformanceList = ArrayAdapter(this, R.layout.selectable_criteria_list, performanceList)
        binding.criteriaPerformanceEditText.setAdapter(adapterPerformanceList)

        val safetyList = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val adapterSafetyList = ArrayAdapter(this, R.layout.selectable_criteria_list, safetyList)
        binding.criteriaSafetyEditText.setAdapter(adapterSafetyList)

        val gearboxList = listOf("Farketmez", "Otomatik", "Manuel")
        val adapterGearboxList = ArrayAdapter(this, R.layout.selectable_criteria_list, gearboxList)
        binding.criteriaGearboxEditText.setAdapter(adapterGearboxList)

        val chassisTypeList = listOf("Farketmez", "Coupe", "Hatchback", "Sedan", "SUV")
        val adapterChassisTypeList = ArrayAdapter(this, R.layout.selectable_criteria_list, chassisTypeList)
        binding.criteriaChassisTypeEditText.setAdapter(adapterChassisTypeList)

        val wheelDriveList = listOf("Farketmez", "Önden", "Arkadan", "4x4")
        val adapterWheelDriveList = ArrayAdapter(this, R.layout.selectable_criteria_list, wheelDriveList)
        binding.criteriaWheelDriveEditText.setAdapter(adapterWheelDriveList)

    }


    //get values entered by user
    private fun getEnteredData() {
        baggageSize = binding.criteriaBaggageSizeEditText.text.toString().trim()
        fuelEfficiency = binding.criteriaFuelEfficiencyEditText.text.toString().trim()
        performance = binding.criteriaPerformanceEditText.text.toString().trim()
        safety = binding.criteriaSafetyEditText.text.toString().trim()
        gearbox = binding.criteriaGearboxEditText.text.toString().trim()
        chassisType = binding.criteriaChassisTypeEditText.text.toString().trim()
        wheelDrive = binding.criteriaWheelDriveEditText.text.toString().trim()
        minPrice = binding.criteriaMinPriceEditText.text.toString().trim()
        maxPrice = binding.criteriaMaxPriceEditText.text.toString().trim()
    }


    //post criteria data
    private fun postCriteriaData() {

        progressDialog.show()

        getEnteredData()

        if (baggageSize == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaBaggageSizeEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (fuelEfficiency == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaFuelEfficiencyEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (performance == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaPerformanceEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (safety == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaSafetyEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (gearbox == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaGearboxEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (chassisType == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaChassisTypeEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (wheelDrive == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaWheelDriveEditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (minPrice == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaMinPriceEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (maxPrice == "") {

            if (progressDialog.isShowing) progressDialog.dismiss()
            binding.criteriaMaxPriceEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else {

            val retrofit = RetrofitInstance.apiCarSuggestion

            val call: Call<CarSuggestion> = retrofit.postSuggestionCriteria("Token $userToken", baggageSize.toInt(), fuelEfficiency.toInt(), performance.toInt(), safety.toInt(), gearbox, chassisType, wheelDrive, minPrice, maxPrice)
            call.enqueue(object : Callback<CarSuggestion> {
                override fun onResponse(call: Call<CarSuggestion>, response: Response<CarSuggestion>) {

                    if (progressDialog.isShowing) progressDialog.dismiss()

                    val intent = Intent(applicationContext, SuggestedCarsActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

                override fun onFailure(call: Call<CarSuggestion>, t: Throwable) {

                    Log.e("Car Suggestion Error", t.printStackTrace().toString())
                    Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
                }
            })

        }

    }


    //back to brandsActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}