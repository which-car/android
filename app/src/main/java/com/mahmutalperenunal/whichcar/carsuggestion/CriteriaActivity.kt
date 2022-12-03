package com.mahmutalperenunal.whichcar.carsuggestion

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.databinding.ActivityCriteriaBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity

class CriteriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriteriaBinding

    private var baggageSize: Int = 0
    private var fuelEfficiency: Int = 0
    private var performance: Int = 0
    private var safety: Int = 0
    private var gearbox: String = ""
    private var chassisType: String = ""
    private var wheelDrive: String = ""
    private var minPrice: String = ""
    private var maxPrice: String = ""

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriteriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //animation to show until car suggested
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Öneriler Hazırlanıyor...")

        setSelectableData()

        //checkConnection()


        //go to suggestedCarActivity
        binding.criteriaSuggestButton.setOnClickListener {
            val intent = Intent(applicationContext, SuggestedCarsActivity::class.java)
            startActivity(intent)
            finish()
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        //back to criteriaActivity
        binding.criteriaBackButton.setOnClickListener { onBackPressed() }
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
        baggageSize = binding.criteriaBaggageSizeEditText.text.toString().toInt()
        fuelEfficiency = binding.criteriaFuelEfficiencyEditText.text.toString().toInt()
        performance = binding.criteriaPerformanceEditText.text.toString().toInt()
        safety = binding.criteriaSafetyEditText.text.toString().toInt()
        gearbox = binding.criteriaGearboxEditText.text.toString().trim()
        chassisType = binding.criteriaChassisTypeEditText.text.toString().trim()
        wheelDrive = binding.criteriaWheelDriveEditText.text.toString().trim()
        minPrice = binding.criteriaMinPriceEditText.text.toString().trim()
        maxPrice = binding.criteriaMaxPriceEditText.text.toString().trim()
    }


    //post brand data
    /*private fun postBrandData() {

        progressDialog.show()

        getEnteredData

        if (baggageSize.equals("")) {

            binding.criteriaBaggageSizeEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (fuelEfficiency.equals("")) {

            binding.criteriaFuelEfficiencyEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (performance.equals("")) {

            binding.criteriaPerformanceEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (safety.equals("")) {

            binding.criteriaSafetyEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (gearbox == "") {

            binding.criteriaGearboxEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (chassisType == "") {

            binding.criteriaChassisTypeEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (wheelDrive == "") {

            binding.criteriaWheelDriveEditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (minPrice == "") {

            binding.criteriaMinPriceEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else if (maxPrice == "") {

            binding.criteriaMaxPriceEditTextLayout.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen Tüm Alanları Doldurun!", Toast.LENGTH_SHORT).show()

        } else {

            //val retrofit = RetrofitInstance.apiGallery

            //val call: Call<Images> = retrofit.postGalleryItem("Token $userToken", brand)
            /*call.enqueue(object : Callback<Images> {
                override fun onResponse(call: Call<Images>, response: Response<Images>) {

                    if (progressDialog.isShowing) progressDialog.dismiss()

                    Toast.makeText(applicationContext, "Kullanıcı Kaydı Oluşturuldu!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(applicationContext, SuggestedCarsActivity::class.java)
                    startActivity(intent)
                    finish()
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }

                override fun onFailure(call: Call<Images>, t: Throwable) {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Log.e("Gallery Add Error", t.printStackTrace().toString())
                    Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
                }
            })*/

        }

    }*/


    //back to brandsActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}