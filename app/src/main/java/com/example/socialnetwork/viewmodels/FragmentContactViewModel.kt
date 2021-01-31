package com.example.socialnetwork.viewmodels

import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialnetwork.adapter.mainRecAdapter
import com.example.socialnetwork.model.MainRecObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentContactViewModel : ViewModel() {
    lateinit var RootRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var RootRefUser: DatabaseReference
    var currentUserKey: String? = null

    val _contactList = MutableLiveData<ArrayList<MainRecObject>>()
    val contactList: LiveData<ArrayList<MainRecObject>> get() = _contactList

    init {
        mAuth = FirebaseAuth.getInstance()
        currentUserKey = mAuth.currentUser?.uid.toString()
        RootRef = FirebaseDatabase.getInstance().getReference().child("Contacts")
        RootRefUser = FirebaseDatabase.getInstance().getReference().child("Users")
    getContectUserKey()
    }

    fun getContectUserKey() {
        var currentList = arrayListOf<MainRecObject>()
        RootRef.child(currentUserKey.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentList.clear()
                _contactList.value?.clear()
                contactList.value?.clear()
                val iterator = snapshot.children.iterator()
                while (iterator.hasNext()) {
                    val contactKey = iterator.next().key.toString()
                    RootRefUser.child(contactKey)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                currentList.clear()
                                _contactList.value?.clear()
                                contactList.value?.clear()
                                val user: MainRecObject? =
                                    snapshot.getValue(MainRecObject::class.java)
                                Log.d("key", user.toString())
                                if (user != null) {
                                    currentList.add(user)
                                }
                                _contactList.value = currentList
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun observe() {
        RootRef.child(currentUserKey.toString()).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                getContectUserKey()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                getContectUserKey()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                      getContectUserKey()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                     getContectUserKey()
            }

            override fun onCancelled(error: DatabaseError) {
                     getContectUserKey()
            }

        })
    }
}