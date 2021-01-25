package com.example.socialnetwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.socialnetwork.databinding.ActivityCreateAccountEmailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateAccountEmail : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var RootRef: DatabaseReference
    lateinit var binding: ActivityCreateAccountEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account_email)
        mAuth = FirebaseAuth.getInstance()
        RootRef = FirebaseDatabase.getInstance().getReference()
        binding.btnCreateAccount.setOnClickListener {
            CreateNewAccount()
        }
    }

    private fun CreateNewAccount() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "email empty", Toast.LENGTH_SHORT).show()
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "password empty", Toast.LENGTH_SHORT).show()
        } else {
            binding.progressBar.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUserId = mAuth.currentUser?.uid
                        val profileMap: HashMap<String, String> = HashMap()
                        profileMap.put("profileImage", "")
                        profileMap.put("uid", currentUserId.toString())
                        profileMap.put("name", "")
                        profileMap.put("phoneNumber",email)
                        RootRef.child("Users").child(currentUserId.toString()).setValue(profileMap)
                        binding.progressBar.visibility = View.INVISIBLE
                        SendUserToLoginActivity(email, password)
                        finish()
                    }
                }
        }
    }

    private fun SendUserToLoginActivity(email: String, password: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startActivity(intent)
    }
}