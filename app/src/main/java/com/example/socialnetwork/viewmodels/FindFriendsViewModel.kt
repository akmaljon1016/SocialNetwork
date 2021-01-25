package com.example.socialnetwork.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Database
import com.example.socialnetwork.adapter.mainRecAdapter
import com.example.socialnetwork.model.MainRecObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FindFriendsViewModel : ViewModel() {

    lateinit var RootRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth

    var _findFriendList = MutableLiveData<ArrayList<MainRecObject>>()
    val findFriendList: LiveData<ArrayList<MainRecObject>> get() = _findFriendList

    init {
        mAuth = FirebaseAuth.getInstance()
        RootRef = FirebaseDatabase.getInstance().getReference("Users")
        setFindFriendsList()
    }

    fun setFindFriendsList() {
        val tempList = arrayListOf<MainRecObject>()
        RootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tempList.clear()
                for (postSnapShot: DataSnapshot in snapshot.children) {
                   // val name = postSnapShot.getValue(MainRecObject::class.java)
                    val user: MainRecObject? = postSnapShot.getValue(MainRecObject::class.java)
                    if (user != null) {
                        tempList.add(user)
                    }
                     _findFriendList.value = tempList
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

