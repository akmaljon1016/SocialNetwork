package com.example.socialnetwork.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.mainRecAdapter
import com.example.socialnetwork.databinding.FragmentRequestBinding
import com.example.socialnetwork.model.MainRecObject
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class FragmentRequest : Fragment() {

    lateinit var binding: FragmentRequestBinding
    lateinit var ChatRequestRef: DatabaseReference
    lateinit var RootUserRef: DatabaseReference
    lateinit var ContactsRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    private var currentUserId: String? = null
    var list_user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_request, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests")
        RootUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts")
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)

    }

    override fun onStart() {
        super.onStart()
        val option: FirebaseRecyclerOptions<MainRecObject> =
            FirebaseRecyclerOptions.Builder<MainRecObject>()
                .setQuery(ChatRequestRef.child(currentUserId.toString()), MainRecObject::class.java)
                .build()
        var adapter: FirebaseRecyclerAdapter<MainRecObject, RequestViewHolder> =
            object : FirebaseRecyclerAdapter<MainRecObject, RequestViewHolder>(option) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RequestViewHolder {
                    return RequestViewHolder(
                        LayoutInflater.from(context)
                            .inflate(R.layout.recyclerview_item, parent, false)
                    )
                }

                override fun onBindViewHolder(
                    holder: RequestViewHolder,
                    position: Int,
                    model: MainRecObject
                ) {
                    holder.request_accept_button.visibility = View.VISIBLE
                    holder.request_cancel_button.visibility = View.VISIBLE
                    holder.request_accept_button.setOnClickListener {
                        AcceptRequest()
                    }
                    holder.request_cancel_button.setOnClickListener {
                        CancelRequest()
                    }
                    list_user_id = getRef(position).key
                    val getTypeRef: DatabaseReference = getRef(position).child("request_type").ref
                    getTypeRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val type: String = snapshot.getValue().toString()
                                if (type.equals("recieved")) {
                                    RootUserRef.child(list_user_id.toString())
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val requester: MainRecObject? =
                                                    snapshot.getValue(MainRecObject::class.java)
                                                Glide.with(holder.rec_main_image)
                                                    .load(requester?.profileImage)
                                                    .placeholder(R.drawable.person)
                                                    .into(holder.rec_main_image)
                                                holder.rec_main_username.setText(requester?.name)
                                                holder.rec_main_phoneNumber.setText(requester?.phoneNumber)
                                            }

                                            override fun onCancelled(error: DatabaseError) {

                                            }

                                        })
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }
            }
        binding.recyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun CancelRequest() {
        ChatRequestRef.child(currentUserId.toString()).child(list_user_id.toString()).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ChatRequestRef.child(list_user_id.toString()).child(currentUserId.toString())
                        .removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(requireContext(), "Request Cancelled", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }

    private fun AcceptRequest() {
        ContactsRef.child(currentUserId.toString()).child(list_user_id.toString()).child("Contacts")
            .setValue("Saved").addOnCompleteListener {
                if (it.isSuccessful) {
                    ContactsRef.child(list_user_id.toString()).child(currentUserId.toString())
                        .child("Contacts").setValue("Saved").addOnCompleteListener {
                            if (it.isSuccessful) {
                                ChatRequestRef.child(currentUserId.toString())
                                    .child(list_user_id.toString()).removeValue()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            ChatRequestRef.child(list_user_id.toString())
                                                .child(currentUserId.toString()).removeValue()
                                                .addOnCompleteListener {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Accepted",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
    }

    public class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rec_main_image = view.findViewById<ImageView>(R.id.rec_item_image)
        val rec_main_username = view.findViewById<TextView>(R.id.rec_item_userName)
        val rec_main_phoneNumber = view.findViewById<TextView>(R.id.rec_item_phoneNumber)
        val rec_main_clock = view.findViewById<TextView>(R.id.rec_item_clock)
        val request_accept_button = view.findViewById<Button>(R.id.request_accept_button)
        val request_cancel_button = view.findViewById<Button>(R.id.request_cancel_button)
    }
}