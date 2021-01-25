package com.example.socialnetwork

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.socialnetwork.viewmodels.ProfileViewModel
import com.example.socialnetwork.databinding.ActivityProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


const val GalleryPick: Int = 1

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var viewmodel: ProfileViewModel
    lateinit var UserProfileImageRef: StorageReference
    lateinit var auth: FirebaseAuth
    lateinit var currentUserId: String
    lateinit var RootRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewmodel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser!!.uid
        RootRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId)



        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        downloadImage()
        binding.profileOkButton.setOnClickListener {
            Toast.makeText(this, viewmodel.currentUserId.toString(), Toast.LENGTH_SHORT).show()
            viewmodel.profileOK(binding.profileName.text.toString())
        }
        binding.profileImage.setOnClickListener {
            intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, GalleryPick)
        }
//        viewmodel.profileName.observe(this, Observer {
//            binding.profileName.setText(it)
//        })
        binding.profileViewModels = viewmodel
        binding.lifecycleOwner = this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryPick && resultCode == RESULT_OK) {
            data.let {
                val imageUrl = it?.data
                binding.profileImage.setImageURI(imageUrl)
                Glide.with(applicationContext).load(imageUrl)
                    .dontAnimate()
                    .into(binding.profileImage)
                Toast.makeText(this, imageUrl.toString(), Toast.LENGTH_SHORT).show()
//                CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1, 1)
//                    .start(this)
                val filePath: StorageReference = UserProfileImageRef.child(currentUserId + ".jpg")
                if (imageUrl != null) {
                    filePath.putFile(imageUrl).addOnFailureListener {
                        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {
                        val result: Task<Uri> = it.storage.downloadUrl
                        result.addOnSuccessListener {
                            val imageUrl: String = it.toString()
                            RootRef.child("profileImage").setValue(imageUrl)
                        }
                        downloadImage()
                    }
                }
            }
        }
    }

    fun downloadImage() {
        RootRef
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl: String = snapshot.child("profileImage").value.toString()

                    binding.textView.setText(imageUrl)
                    Glide.with(baseContext).load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .placeholder(R.drawable.avatar)
                        .fitCenter()
                        .into(binding.profileImage)
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}
