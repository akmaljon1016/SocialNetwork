package com.example.socialnetwork.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialnetwork.ChatActivity
import com.example.socialnetwork.R
import com.example.socialnetwork.databinding.FragmentChatBinding
import com.example.socialnetwork.model.MainRecObject
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_send_request.*

class FragmentChat : Fragment() {

    lateinit var binding: FragmentChatBinding
    lateinit var Chatref: DatabaseReference
    lateinit var UserRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    private var currentUserId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        val option: FirebaseRecyclerOptions<MainRecObject> =
            FirebaseRecyclerOptions.Builder<MainRecObject>()
                .setQuery(Chatref, MainRecObject::class.java)
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
                    UserRef.child(list_user_id.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot.child("userState").hasChild("state")) {
                                    val state: String =
                                        snapshot.child("userState").child("state").getValue()
                                            .toString()
                                    val date: String =
                                        snapshot.child("userState").child("date").getValue()
                                            .toString()
                                    val time: String =
                                        snapshot.child("userState").child("time").getValue()
                                            .toString()
                                    if (state.equals("online")){
                                        holder.rec_main_phoneNumber.setText("online")
                                        holder.rec_main_onlineImage.visibility=View.VISIBLE
                                    }
                                    else if (state.equals("offline")){
                                        holder.rec_main_phoneNumber.setText("Last seen:"+date+" "+time)
                                        holder.rec_main_onlineImage.visibility=View.INVISIBLE
                                    }
                                } else {
                                    holder.rec_main_phoneNumber.setText("offline")
                                }
                                val requester: MainRecObject? =
                                    snapshot.getValue(MainRecObject::class.java)
                                Glide.with(holder.rec_main_image)
                                    .load(requester?.profileImage)
                                    .placeholder(R.drawable.person)
                                    .into(holder.rec_main_image)
                                holder.rec_main_username.setText(requester?.name)
                                holder.itemView.setOnClickListener {
                                    val intent = Intent(requireContext(), ChatActivity::class.java)
                                    intent.putExtra("visit_user_id", list_user_id)
                                    intent.putExtra("visit_user_name", requester?.name)
                                    startActivity(intent)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }

            }
        binding.recyclerview.adapter = adapter
        adapter.startListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid
        Chatref = FirebaseDatabase.getInstance().getReference().child("Contacts")
            .child(currentUserId.toString())
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val rec_main_onlineImage=view.findViewById<ImageView>(R.id.onlineImage)
    }
}