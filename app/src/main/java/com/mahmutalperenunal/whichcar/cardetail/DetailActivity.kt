package com.mahmutalperenunal.whichcar.cardetail

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.adapter.CarImageAdapter
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityDetailBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import com.mahmutalperenunal.whichcar.model.CarDetail
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import me.relex.circleindicator.CircleIndicator3
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var  viewPager2: ViewPager2
    private lateinit var imageList:ArrayList<Int>

    private var brand: String = ""
    private var model: String = ""

    private var userToken: String? = null

    private var like: Int = 10
    private var unlike: Int = 10

    private var carFeedback: String = ""

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


        //viewpager
        viewPager2 = findViewById(R.id.detail_viewPager)
        imageList = ArrayList()

        imageList.add(R.drawable.car)
        imageList.add(R.drawable.car2)

        binding.detailViewPager.adapter = CarImageAdapter(imageList, viewPager2)
        binding.detailViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val indicator: CircleIndicator3 = binding.detailCircleIndicator
        indicator.setViewPager(binding.detailViewPager)

        //fake swipe
        binding.detailNextToButton.setOnClickListener {
            binding.detailViewPager.apply {
                beginFakeDrag()
                fakeDragBy(-10f)
                endFakeDrag()
            }
        }

        binding.detailBackToButton.setOnClickListener {
            binding.detailViewPager.apply {
                beginFakeDrag()
                fakeDragBy(10f)
                endFakeDrag()
            }
        }


        //add to favorites
        binding.detailAddFavoriteButton.setOnClickListener { addToFavorites() }

        //back
        binding.detailBackButton.setOnClickListener { onBackPressed() }
    }


    //check connection
    private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (!isConnected) {
                AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.no_internet_connection_title_text)
                    .setMessage(R.string.no_internet_connection_description_text)
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

                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
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

                Toast.makeText(applicationContext, R.string.car_added_to_favorites_text, Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                Log.e("Car Detail Error", t.printStackTrace().toString())

                Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
            }
        })

    }


    //send like and unlike data
    private fun postLikeUnlikeData() {

        binding.detailLikeButton.setOnClickListener {

            when (carFeedback) {
                "" -> {

                    AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setTitle(R.string.suggestion_title_text)
                        .setMessage(R.string.user_suggestion_description_text)
                        .setIcon(R.drawable.question)
                        .setPositiveButton(R.string.recommend_text) { dialog, _ ->

                            val retrofit = RetrofitInstance.apiCarDetail

                            val call: Call<CarDetail> = retrofit.postRecommendData("Token $userToken", brand, model)
                            call.enqueue(object : Callback<CarDetail> {
                                override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {

                                    carFeedback = "Like"
                                    like++
                                    binding.detailLikeTextView.text = like.toString()
                                    binding.detailLikeButton.setImageResource(R.drawable.like)

                                    Toast.makeText(applicationContext, R.string.car_recommended_text, Toast.LENGTH_SHORT).show()

                                }

                                override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                                    Log.e("Car Like Error", t.printStackTrace().toString())

                                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
                                }
                            })

                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
                "Unlike" -> {

                    AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setTitle(R.string.suggestion_title_text)
                        .setMessage(R.string.user_suggestion_description_text2)
                        .setIcon(R.drawable.question)
                        .setPositiveButton(R.string.recommend_text) { dialog, _ ->

                            val retrofit = RetrofitInstance.apiCarDetail

                            val call: Call<CarDetail> = retrofit.postRecommendData("Token $userToken", brand, model)
                            call.enqueue(object : Callback<CarDetail> {
                                override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {

                                    carFeedback = "Like"
                                    like++
                                    unlike--
                                    binding.detailLikeTextView.text = like.toString()
                                    binding.detailUnlikeTextView.text = unlike.toString()
                                    binding.detailLikeButton.setImageResource(R.drawable.like)
                                    binding.detailUnlikeButton.setImageResource(R.drawable.bad_quality)

                                    Toast.makeText(applicationContext, R.string.car_recommended_text, Toast.LENGTH_SHORT).show()

                                }

                                override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                                    Log.e("Car Like Error", t.printStackTrace().toString())

                                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
                                }
                            })

                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
                else -> {

                    AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setTitle(R.string.suggestion_title_text)
                        .setMessage(R.string.user_suggestion_description_text3)
                        .setIcon(R.drawable.question)
                        .setPositiveButton(R.string.undo_action_text) { dialog, _ ->

                            val retrofit = RetrofitInstance.apiCarDetail

                            val call: Call<CarDetail> = retrofit.postRecommendData("Token $userToken", brand, model)
                            call.enqueue(object : Callback<CarDetail> {
                                override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {

                                    carFeedback = ""
                                    like--
                                    binding.detailLikeTextView.text = like.toString()
                                    binding.detailLikeButton.setImageResource(R.drawable.good_quality)

                                    Toast.makeText(applicationContext, R.string.car_recommended_text2, Toast.LENGTH_SHORT).show()

                                }

                                override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                                    Log.e("Car Like Error", t.printStackTrace().toString())

                                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
                                }
                            })

                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
            }

        }

        binding.detailUnlikeButton.setOnClickListener {

            when (carFeedback) {
                "" -> {

                    AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setTitle(R.string.suggestion_title_text)
                        .setMessage(R.string.user_suggestion_description_text4)
                        .setIcon(R.drawable.question)
                        .setPositiveButton(R.string.not_recommend_text) { dialog, _ ->

                            val retrofit = RetrofitInstance.apiCarDetail

                            val call: Call<CarDetail> =
                                retrofit.postRecommendData("Token $userToken", brand, model)
                            call.enqueue(object : Callback<CarDetail> {
                                override fun onResponse(
                                    call: Call<CarDetail>,
                                    response: Response<CarDetail>
                                ) {

                                    carFeedback = "Unlike"
                                    unlike++
                                    binding.detailUnlikeTextView.text = unlike.toString()
                                    binding.detailUnlikeButton.setImageResource(R.drawable.unlike)

                                    Toast.makeText(applicationContext, R.string.car_not_recommended_text, Toast.LENGTH_SHORT).show()

                                }

                                override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                                    Log.e("Car Like Error", t.printStackTrace().toString())

                                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
                                }
                            })

                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
                "Like" -> {

                    AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setTitle(R.string.suggestion_title_text)
                        .setMessage(R.string.user_suggestion_description_text5)
                        .setIcon(R.drawable.question)
                        .setPositiveButton(R.string.not_recommend_text) { dialog, _ ->

                            val retrofit = RetrofitInstance.apiCarDetail

                            val call: Call<CarDetail> =
                                retrofit.postRecommendData("Token $userToken", brand, model)
                            call.enqueue(object : Callback<CarDetail> {
                                override fun onResponse(
                                    call: Call<CarDetail>,
                                    response: Response<CarDetail>
                                ) {

                                    carFeedback = "Unlike"
                                    unlike++
                                    like--
                                    binding.detailUnlikeTextView.text = unlike.toString()
                                    binding.detailLikeTextView.text = like.toString()
                                    binding.detailUnlikeButton.setImageResource(R.drawable.unlike)
                                    binding.detailLikeButton.setImageResource(R.drawable.good_quality)

                                    Toast.makeText(applicationContext, R.string.car_not_recommended_text, Toast.LENGTH_SHORT).show()

                                }

                                override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                                    Log.e("Car Like Error", t.printStackTrace().toString())

                                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
                                }
                            })

                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
                else -> {

                    AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        .setTitle(R.string.suggestion_title_text)
                        .setMessage(R.string.user_suggestion_description_text6)
                        .setIcon(R.drawable.question)
                        .setPositiveButton(R.string.undo_action_text) {
                                dialog, _ ->

                            val retrofit = RetrofitInstance.apiCarDetail

                            val call: Call<CarDetail> =
                                retrofit.postRecommendData("Token $userToken", brand, model)
                            call.enqueue(object : Callback<CarDetail> {
                                override fun onResponse(
                                    call: Call<CarDetail>,
                                    response: Response<CarDetail>
                                ) {

                                    carFeedback = ""
                                    unlike--
                                    binding.detailUnlikeTextView.text = unlike.toString()
                                    binding.detailUnlikeButton.setImageResource(R.drawable.bad_quality)

                                    Toast.makeText(applicationContext, R.string.car_not_recommended_text2, Toast.LENGTH_SHORT).show()

                                }

                                override fun onFailure(call: Call<CarDetail>, t: Throwable) {

                                    Log.e("Car Like Error", t.printStackTrace().toString())

                                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()
                                }
                            })

                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel_text) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                }
            }

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