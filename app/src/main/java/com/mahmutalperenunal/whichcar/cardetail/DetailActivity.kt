package com.mahmutalperenunal.whichcar.cardetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.adapter.detail.CarImageAdapter
import com.mahmutalperenunal.whichcar.databinding.ActivityDetailBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import kotlin.math.abs

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var  viewPager2: ViewPager2
    private lateinit var handler : Handler
    private lateinit var imageList:ArrayList<Int>
    private lateinit var adapter: CarImageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setUpTransformer()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable , 3000)
            }
        })


        //checkConnection()


        binding.detailBackButton.setOnClickListener { onBackPressed() }
    }


    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable , 3000)
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


    //get detail data
    /*private fun getDetailData() {

        val repository = RepositoryGallery()
        val mainViewModelFactory = MainViewModelFactoryGallery(repository)
        mainViewModelGallery = ViewModelProvider(this, mainViewModelFactory)[MainViewModelGallery::class.java]
        mainViewModelGallery.getImage("Token $userToken")
        mainViewModelGallery.getImageRepository.observe(this) { response ->
            if (response.isSuccessful) {

                binding.detailProgressBar.visibility = View.GONE

                response.body()?.let {
                    val size = response.body()!!.size - 1
                    for (item in 0..size) {
                        response.body()!![item].image = "https://kresapp.herokuapp.com" + response.body()!![item].image
                        Log.d("URL", response.body()!![item].image.toString())
                        galleryAdapter.setData(it)
                        imageIds.add(response.body()!![item].id)
                        imageTitles.add(response.body()!![item].imageTitle)
                    }
                }
            } else {
                Log.e("Error", response.code().toString())
                binding.modelsProgressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "İşlem başarısız. Lütfen tekrar deneyin!", Toast.LENGTH_SHORT).show()
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