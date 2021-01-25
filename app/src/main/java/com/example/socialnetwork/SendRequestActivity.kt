package com.example.socialnetwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.socialnetwork.databinding.ActivitySendRequestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_send_request.*

class SendRequestActivity : AppCompatActivity(){

    lateinit var binding: ActivitySendRequestBinding
    lateinit var RootRef: DatabaseReference
    lateinit var ChatRequestRef: DatabaseReference
    lateinit var ContactsRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    private var recieverId: String? = null
    private var current_State: String? = null
    private var senderUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recieverId = intent.extras?.getString("uid").toString()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        RootRef = FirebaseDatabase.getInstance().getReference("Users").child(recieverId!!)
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests")
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts")
        mAuth = FirebaseAuth.getInstance()
        senderUserId = mAuth.currentUser?.uid
        current_State = "new"
        manageRequest()
        RootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name: String = snapshot.child("name").value.toString()
                val phone: String = snapshot.child("phoneNumber").value.toString()
                val image: String = snapshot.child("profileImage").value.toString()
                binding.name.setText(name)
                binding.phoneNumber.setText(phone)
                Glide.with(binding.imageView).load(image)
                    .placeholder(R.drawable.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageView)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        binding.btnSendRequest.setOnClickListener {
            manageRequest()
        }
    }

    fun manageRequest() {
        ChatRequestRef.child(senderUserId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(recieverId.toString())) {
                        val request_type: String =
                            snapshot.child(recieverId.toString())
                                .child("request_type").value.toString()
                        if (request_type.equals("sent")) {
                            current_State = "request_sent"
                            binding.btnSendRequest.setText("Cancel Chat Request")
                        } else if (request_type.equals("recieved")) {
                            current_State = "request_recieved"
                            binding.btnSendRequest.setText("Accept Chat Request")
                            binding.btnRegretRequest.visibility = View.VISIBLE
                            binding.btnRegretRequest.isEnabled = true

                            binding.btnRegretRequest.setOnClickListener {
                                CancelChatRequest()
                            }
                        }
                    } else {
                        ContactsRef.child(senderUserId.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.hasChild(recieverId.toString())){
                                            current_State="friends"
                                        binding.btnSendRequest.setText("Remove this Contact")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        if (!senderUserId.equals(recieverId)) {
            binding.btnSendRequest.setOnClickListener {
                binding.btnSendRequest.isEnabled = false
                if (current_State.equals("new")) {
                    SendChatRequest()
                }
                if (current_State.equals("request_sent")) {
                    Toast.makeText(this, "request_sent", Toast.LENGTH_SHORT).show()
                    CancelChatRequest()
                }
                if (current_State.equals("request_recieved")) {
                    AcceptChatRequest()
                }
                if (current_State.equals("friends")){
                    removeSpecificContact()
                }
            }
        } else {
            binding.btnSendRequest.visibility = View.INVISIBLE
        }
    }

    private fun removeSpecificContact() {
        ContactsRef.child(senderUserId.toString()).child(recieverId.toString()).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ContactsRef.child(recieverId.toString()).child(senderUserId.toString())
                        .removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                binding.btnSendRequest.isEnabled = true
                                current_State = "new"
                                binding.btnSendRequest.setText("Send Request")
                            }
                        }
                }
            }
    }

    private fun AcceptChatRequest() {
        ContactsRef.child(senderUserId.toString()).child(recieverId.toString())
            .child("Contacts").setValue("Saved")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ContactsRef.child(recieverId.toString()).child(senderUserId.toString())
                        .child("Contacts").setValue("Saved")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                ChatRequestRef.child(senderUserId.toString())
                                    .child(recieverId.toString()).removeValue()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            ChatRequestRef.child(recieverId.toString())
                                                .child(senderUserId.toString()).removeValue()
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        binding.btnSendRequest.isEnabled = true
                                                        current_State = "friends"
                                                        binding.btnSendRequest.setText("Remove this Contacts")
                                                        binding.btnRegretRequest.visibility =
                                                            View.INVISIBLE
                                                        binding.btnRegretRequest.isEnabled = false
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
    }

    private fun CancelChatRequest() {
        ChatRequestRef.child(senderUserId.toString()).child(recieverId.toString()).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ChatRequestRef.child(recieverId.toString()).child(senderUserId.toString())
                        .removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                binding.btnSendRequest.isEnabled = true
                                current_State = "new"
                                binding.btnSendRequest.setText("Send Request")
                            }
                        }
                }
            }
    }

    private fun SendChatRequest() {
        ChatRequestRef.child(senderUserId.toString()).child(recieverId.toString())
            .child("request_type")
            .setValue("sent")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ChatRequestRef.child(recieverId.toString()).child(senderUserId.toString())
                        .child("request_type").setValue("recieved")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                btnSendRequest.isEnabled = true
                                current_State = "request_sent"
                                btnSendRequest.setText("Cancel Chat Request")
                            }
                        }
                }
            }
    }
}