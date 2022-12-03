package com.mahmutalperenunal.whichcar.cardetail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahmutalperenunal.whichcar.databinding.ActivityModelsBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import java.io.File

class ModelsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModelsBinding

    private var brand: String = ""
    private lateinit var models: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModelsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get brand
        brand = intent.getStringExtra("Brand").toString()

        models = arrayListOf()

        //checkConnection()

        //postBrandData()
        //onClickProcess()

        //back to brandActivity
        binding.modelsBackButton.setOnClickListener { onBackPressed() }
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


    //post brand data
    /*private fun postBrandData() {

        //val retrofit = RetrofitInstance.apiGallery

        //val call: Call<Images> = retrofit.postGalleryItem("Token $userToken", brand)
        /*call.enqueue(object : Callback<Images> {
            override fun onResponse(call: Call<Images>, response: Response<Images>) {

                Toast.makeText(applicationContext, "Kullanıcı Kaydı Oluşturuldu!", Toast.LENGTH_SHORT).show()

                getModelData()

            }

            override fun onFailure(call: Call<Images>, t: Throwable) {
                Log.e("Gallery Add Error", t.printStackTrace().toString())
                Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
            }
        })*/

    }*/


    //get model data
    /*private fun getModelData() {

        setupGalleryRecyclerview()

        val repository = RepositoryGallery()
        val mainViewModelFactory = MainViewModelFactoryGallery(repository)
        mainViewModelGallery = ViewModelProvider(this, mainViewModelFactory)[MainViewModelGallery::class.java]
        mainViewModelGallery.getImage("Token $userToken")
        mainViewModelGallery.getImageRepository.observe(this) { response ->
            if (response.isSuccessful) {

                binding.modelsProgressBar.visibility = View.GONE

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
                binding.modelsNoModelImageView.visibility = View.VISIBLE
                binding.modelsNoModelTextView.visibility = View.VISIBLE
                Toast.makeText(applicationContext, "İşlem başarısız. Lütfen tekrar deneyin!", Toast.LENGTH_SHORT).show()
            }
        }

    }*/


    //setup recyclerView
    /*private fun setupRecyclerview() {
        binding.moduleGalleryMainListRecyclerView.adapter = galleryAdapter
        binding.moduleGalleryMainListRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.moduleGalleryMainListRecyclerView.setHasFixedSize(true)
    }*/


    //go to detailActivity
    /*private fun onClickProcess() {
        galleryAdapter.setOnItemClickListener(object : GalleryAdapter.OnItemClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(position: Int) {

                val intent = Intent(applicationContext, DetailActivity::class.java)
                intent.putExtra("Brand", brand)
                intent.putExtra("Model", models[position])
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }
        })
    }*/


    //back to brandsActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(applicationContext, BrandsActivity::class.java)
        startActivity(intent)
        finish()
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}