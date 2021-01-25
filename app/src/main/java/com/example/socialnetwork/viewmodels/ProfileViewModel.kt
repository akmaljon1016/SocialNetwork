package com.example.socialnetwork.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileViewModel : ViewModel() {

    var auth: FirebaseAuth
    var currentUserId: String
    var RootRef: DatabaseReference

    val _profileImage = MutableLiveData<String>()
    val profileImage: LiveData<String> get() = _profileImage
    val _profileName = MutableLiveData<String>()
    val profileName: LiveData<String> get() = _profileName

    init {
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser!!.uid
        RootRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId)

        RootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _profileName.value = snapshot.child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun profileOK(name:String) {

        RootRef.child("name").setValue(name)

    }
}