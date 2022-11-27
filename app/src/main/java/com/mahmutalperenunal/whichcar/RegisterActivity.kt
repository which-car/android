package com.mahmutalperenunal.whichcar

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.mahmutalperenunal.whichcar.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var username: String = ""
    private var password1: String = ""
    private var password2: String = ""
    private var email: String = ""

    private var passwordControl: Boolean = false
    private var emailControl: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //checkConnection()

        validEmail()
        validPassword()

        //register
        binding.registerRegisterButton.setOnClickListener {  }

        //back to loginActivity
        binding.registerBackButton.setOnClickListener { onBackPressed() }
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


    //register process
    /*private fun registerProcess() {

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

                val repository = RepositoryRegister()
                val mainViewModelFactory = MainViewModelFactoryRegister(repository)
                mainViewModelRegister = ViewModelProvider(this, mainViewModelFactory)[MainViewModelRegister::class.java]
                val postUser = Registration(username, password1, password2)
                mainViewModelRegister.postRegisterRequest(postUser)
                mainViewModelRegister.postRegisterRequestRepository.observe(viewLifecycleOwner) { response ->
                    if (response.isSuccessful) {

                        val intentLogin = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intentLogin)
                        finish()
                        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

                        Toast.makeText(applicationContext, "Kayıt İşlemi Tamamlandı! Lütfen Giriş Yapın.", Toast.LENGTH_SHORT).show()

                    } else {
                        Log.e("User Post Error", response.code().toString())
                        Toast.makeText(applicationContext, "Aynı İsimde Kullanıcı Tanımlı!", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }*/


    //back to loginActivity
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intentLogin = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intentLogin)
        finish()
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}