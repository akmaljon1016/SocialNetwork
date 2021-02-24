package com.example.socialnetwork

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.socialnetwork.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var email: String? = null
    var password: String? = null
    var mAuth: FirebaseAuth? = null
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        email = intent.extras?.getString("email")?.trim()
        password = intent.extras?.getString("password")?.trim()

        binding.email.setText(email)
        binding.password.setText(password)

        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            mAuth!!.signInWithEmailAndPassword(
                binding.email.text.trim().toString(),
                binding.password.text.trim().toString()
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        binding.progressBar.visibility = View.INVISIBLE
                        savePrefData()
                        finish()
                    } else {
                        Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }
        }
    }

    private fun savePrefData() {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean("isCreated", true)
        editor.apply()
    }
}