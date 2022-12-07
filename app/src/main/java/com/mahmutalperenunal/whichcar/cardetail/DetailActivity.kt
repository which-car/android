package com.mahmutalperenunal.whichcar.cardetail

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.adapter.detail.CarImageAdapter
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityDetailBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import com.mahmutalperenunal.whichcar.model.CarDetail
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var  viewPager2: ViewPager2
    private lateinit var handler : Handler
    private lateinit var imageList:ArrayList<Int>
    private lateinit var adapter: CarImageAdapter

    private var brand: String = ""
    private var model: String = ""

    private var userToken: String? = null

    private var like: Int = 10
    private var unlike: Int = 10

    private lateinit var sharedPreferencesAuthToken: SharedPreferences
    private lateinit var sharedPreferencesUsernamePassword: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get data from modelsActivity
        brand = intent.getStringExtra("Brand").toString()
        model = intent.getStringExtra("Model").toString()


        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)
        sharedPreferencesUsernamePassword = getSharedPreferences("autoUsernamePassword", MODE_PRIVATE)

        userToken = sharedPreferencesAuthToken.getString("token", null)

        setFavoriteButtonVisibility()

        checkConnection()

        getDetailData()

        postLikeUnlikeData()


        //set image animation
        init()
        setUpTransformer()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable , 3000)
            }
        })


        //add to favorites
        binding.detailAddFavoriteButton.setOnClickListener { addToFavorites() }

        //back
        binding.detailBackButton.setOnClickListener { onBackPressed() }
    }


    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable , 5000)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun setUpTransformer(){
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 1.55f + r * 1.65f
        }

        viewPager2.setPageTransformer(transformer)
    }

    private fun init(){
        viewPager2 = findViewById(R.id.detail_viewPager)
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        imageList.add(R.drawable.car)
        imageList.add(R.drawable.car2)

        adapter = CarImageAdapter(imageList, viewPager2)

        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

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


    //get detail data
    private fun getDetailData() {

        val retrofit = RetrofitInstance.apiCarDetail

        val call: Call<CarDetail> = retrofit.getCarDetail("Token $userToken", brand, model)
        call.enqueue(object : Callback<CarDetail> {
            override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {

                binding.detailProgressBar.visibility = View.GONE

                //set data
                binding.detailBrandTextView.text = brand
                binding.detailModelTextView.text = model
                /*binding.detailAccelerationTextView.text = response.body()!!.acceleration
                binding.detailBaggageSizeTextView.text = response.body()!!.baggageSize
                binding.detailChassisTypeTextView.text = response.body()!!.chassisType
                binding.detailEmissionResultTextView.text = response.body()!!.emissionResult
                binding.detailEngineTextView.text = response.body()!!.engine
                binding.detailFuelEfficiencyTextView.text = response.body()!!.fuelEfficiency
                binding.detailGearboxTextView.text = response.body()!!.gearbox
                binding.detailEuroNcapResultTextView.text = response.body()!!.safety
                binding.detailPriceTextView.text = response.body()!!.price
                binding.detailSegmentTextView.text = response.body()!!.segment
                binding.detailLikeTextView.text = response.body()!!.like
                binding.detailUnlikeTextView.text = response.body()!!.unlike

                val carPhoto = findViewById<ImageView>(R.id.car_container_imageView)

                Glide.with(applicationContext)
                    .load(response.body()!!.carPhoto)
                    .centerCrop()
                    .into(carPhoto)*/

            }

            override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                Log.e("Car Detail Error", t.printStackTrace().toString())

                binding.detailProgressBar.visibility = View.GONE

                Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
            }
        })

    }


    //set favorite button visibility
    private fun setFavoriteButtonVisibility() {
        if (sharedPreferencesUsernamePassword.getString("username", null) != "") {
            binding.detailAddFavoriteButton.visibility = View.VISIBLE
        } else {
            binding.detailAddFavoriteButton.visibility = View.GONE
        }
    }


    //add to favorites
    private fun addToFavorites() {

        val retrofit = RetrofitInstance.apiFavorites

        val call: Call<CarDetail> = retrofit.postFavorite("Token $userToken", 1)
        call.enqueue(object : Callback<CarDetail> {
            override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {

                Toast.makeText(applicationContext, "Araç Favorilere Eklendi!", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                Log.e("Car Detail Error", t.printStackTrace().toString())

                Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
            }
        })

    }


    //send like and unlike data
    private fun postLikeUnlikeData() {

        binding.detailLikeButton.setOnClickListener {

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Öneri")
                .setMessage("Bu aracı diğer kullanıcılar için öneriyor musunuz?.")
                .setIcon(R.drawable.question)
                .setPositiveButton("Öneriyorum") {
                        dialog, _ ->

                    like++
                    binding.detailLikeTextView.text = like.toString()

                    val retrofit = RetrofitInstance.apiCarDetail

                    val call: Call<CarDetail> = retrofit.postRecommendData("Token $userToken", brand, model)
                    call.enqueue(object : Callback<CarDetail> {
                        override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {

                            Toast.makeText(applicationContext, "Araç Önerildi!", Toast.LENGTH_SHORT).show()

                        }

                        override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                            Log.e("Car Like Error", t.printStackTrace().toString())

                            Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
                        }
                    })

                    dialog.dismiss()
                }
                .setNegativeButton("İptal") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

        }

        binding.detailUnlikeButton.setOnClickListener {

            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Öneri")
                .setMessage("Bu aracı diğer kullanıcılara önermiyor musunuz?.")
                .setIcon(R.drawable.question)
                .setPositiveButton("Önermiyorum") { dialog, _ ->

                    unlike++
                    binding.detailUnlikeTextView.text = unlike.toString()

                    val retrofit = RetrofitInstance.apiCarDetail

                    val call: Call<CarDetail> =
                        retrofit.postRecommendData("Token $userToken", brand, model)
                    call.enqueue(object : Callback<CarDetail> {
                        override fun onResponse(
                            call: Call<CarDetail>,
                            response: Response<CarDetail>
                        ) {

                            Toast.makeText(applicationContext, "Araç Önerilmedi!", Toast.LENGTH_SHORT).show()

                        }

                        override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                            Log.e("Car Like Error", t.printStackTrace().toString())

                            Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
                        }
                    })

                    dialog.dismiss()
                }
                .setNegativeButton("İptal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

        }

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