package com.example.socialnetwork.Fragments

import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.mainRecAdapter
import com.example.socialnetwork.databinding.FragmentContactsBinding
import com.example.socialnetwork.model.MainRecObject
import com.example.socialnetwork.viewmodels.FragmentContactViewModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class FragmentContacts : Fragment() {
    lateinit var binding: FragmentContactsBinding
    lateinit var ChatRequestRef: DatabaseReference
    lateinit var RootUserRef: DatabaseReference
    lateinit var ContactsRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    private var currentUserId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val option: FirebaseRecyclerOptions<MainRecObject> =
            FirebaseRecyclerOptions.Builder<MainRecObject>()
                .setQuery(ContactsRef.child(currentUserId.toString()), MainRecObject::class.java)
                .build()

        val adapter: FirebaseRecyclerAdapter<MainRecObject, RequestViewHolder> =
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

                   var list_user_id:String = getRef(position).key.toString()
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
        binding.recyclerview.adapter = adapter
        adapter.startListening()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts")
        RootUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
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