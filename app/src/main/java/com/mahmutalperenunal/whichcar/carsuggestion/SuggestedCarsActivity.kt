package com.mahmutalperenunal.whichcar.carsuggestion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mahmutalperenunal.whichcar.cardetail.BrandsActivity
import com.mahmutalperenunal.whichcar.databinding.ActivitySuggestedCarsBinding

class SuggestedCarsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuggestedCarsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuggestedCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //checkConnection()

        //getSuggestedCarsData()

        //onClickProcess()

        //back to criteriaActivity
        binding.suggestedCarBackButton.setOnClickListener { onBackPressed() }
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


    //get get suggested cars data
    /*private fun getSuggestedCarsData() {

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
        val intent = Intent(applicationContext, CriteriaActivity::class.java)
        startActivity(intent)
        finish()
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}