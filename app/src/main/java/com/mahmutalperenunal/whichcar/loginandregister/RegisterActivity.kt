package com.mahmutalperenunal.whichcar.loginandregister

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityRegisterBinding
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import com.mahmutalperenunal.whichcar.model.auth.AuthToken
import com.mahmutalperenunal.whichcar.model.auth.Register
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var username: String = ""
    private var password1: String = ""
    private var password2: String = ""
    private var email: String = ""

    private var passwordControl: Boolean = false
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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkConnection()

        checkPermissions()

        validEmail()
        validPassword()


        //profile photo
        binding.registerProfilePhotoImageView.setOnClickListener { setProfilePhoto() }

        //register
        binding.registerRegisterButton.setOnClickListener { registerProcess() }

        //back to loginActivity
        binding.registerBackButton.setOnClickListener { onBackPressed() }
    }


    //check connection
    private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
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

    }


    //get entered data
    private fun getData() {
        username = binding.registerUsernameEditText.text.toString().trim()
        password1 = binding.registerPasswordEditText.text.toString().trim()
        password2 = binding.registerPassword2EditText.text.toString().trim()
        email = binding.registerEmailEditText.text.toString().trim()
    }


    //valid password
    private fun validPassword(){
        binding.registerPassword2EditText.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.registerPasswordEditText.text.toString() != binding.registerPassword2EditText.text.toString()) {
                    binding.registerPasswordEditTextLayout.helperText = "Şifreler Eşleşmiyor!"
                    passwordControl = false
                } else {
                    binding.registerPassword2EditTextLayout.helperText = ""
                    passwordControl = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }


    //valid email
    private fun validEmail(){
        binding.registerEmailEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.registerEmailEditText.text.toString()).matches()) {
                    binding.registerEmailEditTextLayout.helperText = "Geçersiz Mail Adresi!"
                    emailControl = false
                } else {
                    binding.registerEmailEditTextLayout.helperText = ""
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

                    binding.registerProfilePhotoImageView.setImageBitmap(bitmap)

                }

                IMAGE_REQUEST_CODE -> {

                    imageUri = data?.data!!

                    binding.registerProfilePhotoImageView.setImageURI(imageUri)

                }
            }
        }
    }


    //register process
    private fun registerProcess() {

        val imageName = getFileName(imageUri, applicationContext)

        //check edittext
        if (binding.registerUsernameEditText.text!!.isEmpty()) {

            binding.registerUsernameEditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()

        } else if (binding.registerPasswordEditText.text!!.isEmpty() || !passwordControl) {

            binding.registerPasswordEditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()

        } else if (binding.registerPassword2EditText.text!!.isEmpty() || !passwordControl) {

            binding.registerPassword2EditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()

        } else if (binding.registerEmailEditText.text!!.isEmpty() || !emailControl) {

            binding.registerEmailEditText.error = "Zorunlu"
            Toast.makeText(applicationContext, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()

        } else {

            getData()

            //check passwords
            if (password1 != password2) {
                binding.registerPasswordEditText.error = "Hatalı"
                binding.registerPassword2EditText.error = "Hatalı"
                Toast.makeText(applicationContext, "Girilen şifreler uyuşmamaktadır! Lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
            } else {

                val retrofit = RetrofitInstance.apiRegister

                val path: File = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )

                val file = File(path, imageName)

                try {
                    path.mkdirs()
                } catch (e: Exception) {
                    Log.e("Path Error", e.toString())
                }

                val requestFile: RequestBody = RequestBody.create("image/*".toMediaType(), file)
                val image: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val username: RequestBody = RequestBody.create("text/plain".toMediaType(), username)
                val password: RequestBody = RequestBody.create("text/plain".toMediaType(), password1)
                val password2: RequestBody = RequestBody.create("text/plain".toMediaType(), password2)
                val email: RequestBody = RequestBody.create("text/plain".toMediaType(), email)

                val postRegister = Register(username.toString(), password.toString(), password2.toString(), email.toString(), image.toString())

                val call: Call<AuthToken> = retrofit.postRegister(postRegister)
                call.enqueue(object : Callback<AuthToken> {
                    override fun onResponse(call: Call<AuthToken>, response: Response<AuthToken>) {
                        Toast.makeText(applicationContext, "Kullanıcı Kaydı Oluşturuldu!", Toast.LENGTH_SHORT).show()
                        Toast.makeText(applicationContext, "Lütfen Giriş Yapın!", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }

                    override fun onFailure(call: Call<AuthToken>, t: Throwable) {
                        Log.e("Register Error", t.printStackTrace().toString())
                        Toast.makeText(applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT).show()
                    }
                })

            }

        }

    }


    //back to loginActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentLogin = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intentLogin)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}