package com.example.socialnetwork.adapter

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.opengl.Visibility
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.socialnetwork.R
import com.example.socialnetwork.model.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, var userMessagesList: List<Messages>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    //var userMessagesList: List<Messages>? = null
    var mAuth: FirebaseAuth? = null
    var userRef: DatabaseReference? = null
    lateinit var view: View

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderMessageText: TextView = view.findViewById(R.id.sender_message_text)
        val recieverMessageText: TextView = view.findViewById(R.id.reciever_message)
        val recieverProfileImage: CircleImageView = view.findViewById(R.id.message_profile_image)
        val messageSenderPicture: ImageView = view.findViewById(R.id.message_sender_image_view)
        val messageRecieverPicture: ImageView = view.findViewById(R.id.message_receiver_image_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        mAuth = FirebaseAuth.getInstance()
        view = LayoutInflater.from(context).inflate(R.layout.custom_messages_layout, parent, false)
        return MessageViewHolder(view)

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageSenderId: String = mAuth?.currentUser?.uid.toString()
        val message: Messages = userMessagesList!!.get(position)
        val fromUserId: String = message.from
        val fromMessageType: String = message.type
        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(fromUserId)
        userRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val recieverImage: String = snapshot.child("profileImage").value.toString()
                Glide.with(context).load(recieverImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.recieverProfileImage)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        holder.recieverMessageText.visibility = View.GONE  //invisible
        holder.recieverProfileImage.visibility = View.GONE  //invisible
        holder.senderMessageText.visibility = View.GONE
        holder.messageRecieverPicture.visibility = View.GONE
        holder.messageSenderPicture.visibility = View.GONE
        if (fromMessageType.equals("text")) {
            if (fromUserId.equals(messageSenderId)) {
                holder.senderMessageText.visibility = View.VISIBLE
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout)
                holder.senderMessageText.setTextColor(Color.BLACK)
                holder.senderMessageText.setText(message.message + "\n\n" + message.time + "-" + message.date)
            } else {
                // holder.senderMessageText.visibility = View.INVISIBLE       //invisible
                holder.recieverProfileImage.visibility = View.VISIBLE
                holder.recieverMessageText.visibility = View.VISIBLE
                holder.recieverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout)
                holder.recieverMessageText.setTextColor(Color.BLACK)
                holder.recieverMessageText.setText(message.message + "\n\n" + message.time + "-" + message.date)
            }

        }
         else if (fromMessageType.equals("image")) {
            if (fromUserId.equals(messageSenderId)) {
                holder.messageSenderPicture.visibility = View.VISIBLE
                Glide.with(holder.messageSenderPicture).load(message.message)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.messageSenderPicture)
            }
            else{
                holder.recieverProfileImage.visibility=View.VISIBLE
                holder.messageRecieverPicture.visibility=View.VISIBLE
                Glide.with(holder.messageSenderPicture).load(message.message)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.messageSenderPicture)
            }
        }
        else{

        }
    }

    override fun getItemCount(): Int {
        return userMessagesList?.size!!
    }

//    fun setText(list: ArrayList<Messages>) {
//        this.userMessagesList = list
//        notifyDataSetChanged()
//    }
}