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
import com.mahmutalperenunal.whichcar.api.RetrofitInstance
import com.mahmutalperenunal.whichcar.databinding.ActivityProfileEditBinding
import com.mahmutalperenunal.whichcar.home.HomeActivity
import com.mahmutalperenunal.whichcar.model.NetworkConnection
import com.mahmutalperenunal.whichcar.model.User
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

        //get values
        userId = sharedPreferencesUserId.getInt("id", 0)
        userToken = sharedPreferencesAuthToken.getString("token", null)


        checkConnection()

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
    private fun checkConnection() {

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (!isConnected) {
                AlertDialog.Builder(applicationContext, R.style.CustomAlertDialog)
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
                    binding.profileEditEmailEditTextLayout.helperText = resources.getString(R.string.invalid_mail_address_text)
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
            .setTitle(R.string.add_profile_photo_title_text)
            .setMessage(R.string.add_profile_photo_description_text)
            .setIcon(R.drawable.add_profile)
            .setPositiveButton(R.string.gallery_text) {
                    dialog, _ ->
                requestStorage.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                clicked = true
                dialog.dismiss()
            }
            .setNegativeButton(R.string.camera_text) {
                    dialog, _ ->
                requestCamera.launch(android.Manifest.permission.CAMERA)
                clicked = true
                dialog.dismiss()
            }
            .setNeutralButton(R.string.avatar_text) {
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
                Toast.makeText(applicationContext, R.string.not_allowed_text, Toast.LENGTH_SHORT).show()
            }
        }

        requestStorage = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                pickUpImageFromGallery()
            } else {
                Toast.makeText(applicationContext, R.string.not_allowed_text, Toast.LENGTH_SHORT).show()
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

            binding.profileEditUsernameEditText.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(applicationContext, R.string.fill_all_fields_text, Toast.LENGTH_SHORT).show()

        } else if (binding.profileEditEmailEditText.text!!.isEmpty() || !emailControl) {

            binding.profileEditEmailEditText.error = resources.getString(R.string.compulsory_text)
            Toast.makeText(applicationContext, R.string.fill_all_fields_text, Toast.LENGTH_SHORT).show()

        } else {

            val retrofit = RetrofitInstance.apiUser

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

            val username: RequestBody = RequestBody.create("text/plain".toMediaType(), binding.profileEditUsernameEditText.text.toString().trim())
            val email: RequestBody = RequestBody.create("text/plain".toMediaType(), binding.profileEditEmailEditText.text.toString().trim())

            val call: Call<User> = retrofit.putUser("Token $userToken", userId!!, image, username, email)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {

                    Toast.makeText(applicationContext, R.string.user_information_updated_text, Toast.LENGTH_SHORT).show()

                    editorUsername.putString("username", binding.profileEditUsernameEditText.text.toString().trim())

                    val intentHome = Intent(applicationContext, HomeActivity::class.java)
                    intentHome.putExtra("Username", binding.profileEditUsernameEditText.text.toString().trim())
                    startActivity(intentHome)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                }

                override fun onFailure(call: Call<User>, t: Throwable) {

                    Log.e("Profile Put Error", t.printStackTrace().toString())
                    Toast.makeText(applicationContext, R.string.operation_failed_text, Toast.LENGTH_SHORT).show()

                }
            })

        }

    }


    //back to profileActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentProfile = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intentProfile)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}