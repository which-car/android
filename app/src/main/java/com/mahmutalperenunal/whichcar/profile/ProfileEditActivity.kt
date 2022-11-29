package com.mahmutalperenunal.whichcar.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.mahmutalperenunal.whichcar.R
import com.mahmutalperenunal.whichcar.databinding.ActivityProfileEditBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding

    private var username: String? = null
    private var email: String? = null
    private var userId: Int? = null
    private var userToken: String? = null

    private lateinit var sharedPreferencesUsernamePassword: SharedPreferences
    private lateinit var sharedPreferencesUserId: SharedPreferences
    private lateinit var sharedPreferencesAuthToken: SharedPreferences

    private lateinit var editorUsername: SharedPreferences.Editor

    private var emailControl: Boolean = false

    private var clicked: Boolean = false

    private lateinit var imageUri: Uri
    private lateinit var bitmap: Bitmap

    private lateinit var requestCamera: ActivityResultLauncher<String>
    private lateinit var requestStorage: ActivityResultLauncher<String>

    companion object {
        private const val IMAGE_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set sharedPreferences
        sharedPreferencesUsernamePassword = getSharedPreferences("autoUsernamePassword", MODE_PRIVATE)
        sharedPreferencesUserId = getSharedPreferences("userId", MODE_PRIVATE)
        sharedPreferencesAuthToken = getSharedPreferences("authToken", MODE_PRIVATE)

        editorUsername = sharedPreferencesUsernamePassword.edit()


        //checkConnection()

        checkPermissions()

        getAndSetData()

        validEmail()


        //profile photo
        binding.profileEditProfilePhotoImageView.setOnClickListener { setProfilePhoto() }

        //register
        binding.profileEditSaveButton.setOnClickListener { editProcess() }

        //back to profileActivity
        binding.profileEditBackButton.setOnClickListener { onBackPressed() }
    }


    //check connection
    /*private fun checkConnection() {

        val networkConnection = NetworkConnection(requireActivity().applicationContext)
        networkConnection.observe(viewLifecycleOwner, androidx.lifecycle.Observer { isConnected ->
            if (!isConnected) {
                AlertDialog.Builder(applicationContext, R.style.CustomAlertDialog)
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

    }*/


    //get and set user data
    private fun getAndSetData() {

        //get
        username = intent.getStringExtra("username")
        email = intent.getStringExtra("email")

        //set
        binding.profileEditUsernameEditText.setText(username)
        binding.profileEditEmailEditText.setText(email)

    }


    //valid email
    private fun validEmail(){
        binding.profileEditEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.profileEditEmailEditText.text.toString()).matches()) {
                    binding.profileEditEmailEditTextLayout.helperText = "Geçersiz Mail Adresi!"
                    emailControl = false
                } else {
                    binding.profileEditEmailEditTextLayout.helperText = ""
                    emailControl = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }


    //set profile photo
    private fun setProfilePhoto() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Profile Fotoğrafı Ekle")
            .setMessage("Eklemek istediğiniz foroğrafı veya avatarı seçiniz.")
            .setIcon(R.drawable.add_profile)
            .setPositiveButton("Galeri") {
                    dialog, _ ->
                requestStorage.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                clicked = true
                dialog.dismiss()
            }
            .setNegativeButton("Kamera") {
                    dialog, _ ->
                requestCamera.launch(android.Manifest.permission.CAMERA)
                clicked = true
                dialog.dismiss()
            }
            .setNeutralButton("Avatar") {
                    dialog, _ ->
                clicked = true
                dialog.dismiss()
            }
            .create()
            .show()
    }


    //get image file name
    @SuppressLint("Range")
    private fun getFileName(uri: Uri, context: Context): String {
        var res: String? = null

        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }

            if (res == null) {
                res = uri.path
                val cutt: Int = res!!.lastIndexOf('/')
                if (cutt != -1) {
                    res = res.substring(cutt + 1)
                }
            }

        }

        return res!!

    }


    //control permissions
    private fun checkPermissions() {
        requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                pickUpImageFromCamera()
            } else {
                Toast.makeText(applicationContext, "İzin verilmedi!", Toast.LENGTH_SHORT).show()
            }
        }

        requestStorage = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                pickUpImageFromGallery()
            } else {
                Toast.makeText(applicationContext, "İzin verilmedi!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    //convert bitmap to uri
    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format((now))

        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "IMG_$fileName", null)
        imageUri = Uri.parse(path.toString())
        return imageUri
    }



    //pick up picture from camera
    private fun pickUpImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }



    //pick up image from gallery
    private fun pickUpImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    bitmap = data?.extras!!.get("data") as Bitmap

                    getImageUriFromBitmap(applicationContext, bitmap)

                    binding.profileEditProfilePhotoImageView.setImageBitmap(bitmap)

                }

                IMAGE_REQUEST_CODE -> {

                    imageUri = data?.data!!

                    binding.profileEditProfilePhotoImageView.setImageURI(imageUri)

                }
            }
        }
    }


    //save process
    private fun editProcess() {

        val imageName = getFileName(imageUri, applicationContext)

        //check edittext
        if (binding.profileEditUsernameEditText.text!!.isEmpty()) {

            binding.profileEditUsernameEditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()

        } else if (binding.profileEditEmailEditText.text!!.isEmpty() || !emailControl) {

            binding.profileEditEmailEditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()

        } else {

            //val retrofit = RetrofitInstance.apiGallery

            val path: File = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )

            val file = File(path, imageName)

            try {
                path.mkdirs()
            } catch (e: Exception) {
                Log.e("Path Error", e.toString())
            }

            //val requestFile: RequestBody = RequestBody.create("image/*".toMediaType(), file)
            //val image: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

            //val username: RequestBody = RequestBody.create("text/plain".toMediaType(), username)
            //val password: RequestBody = RequestBody.create("text/plain".toMediaType(), password1)
            //val password2: RequestBody = RequestBody.create("text/plain".toMediaType(), password2)
            //val email: RequestBody = RequestBody.create("text/plain".toMediaType(), email)

            //val call: Call<Images> = retrofit.postGalleryItem("Token $userToken", image, title, description, classroom, user)
            /*call.enqueue(object : Callback<Images> {
                override fun onResponse(call: Call<Images>, response: Response<Images>) {
                    Toast.makeText(applicationContext, "Kullanıcı Kaydı Oluşturuldu!", Toast.LENGTH_SHORT).show()

                    editorUsername.putString("username", binding.profileEditUsername.editText.text.toString().trim())

                    val intentHome = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intentHome)
                    finish()
                    //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }

                override fun onFailure(call: Call<Images>, t: Throwable) {
                    Log.e("Gallery Add Error", t.printStackTrace().toString())
                    Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
                }
            })*/

        }

    }


    //back to profileActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentProfile = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intentProfile)
        finish()
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}