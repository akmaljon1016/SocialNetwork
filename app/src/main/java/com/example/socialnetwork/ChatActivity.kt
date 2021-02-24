package com.example.socialnetwork

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialnetwork.adapter.MessageAdapter
import com.example.socialnetwork.databinding.ActivityChatBinding
import com.example.socialnetwork.model.Messages
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask

class ChatActivity : AppCompatActivity() {

    private var recieverId: String? = null
    private var currentUserId: String? = null
    private var name: String? = null
    lateinit var UserRef: DatabaseReference
    lateinit var RootRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var messageList: ArrayList<Messages>
    var linearLayoutManager: LinearLayoutManager? = null
    var messageAdapter: MessageAdapter? = null
    var checker: String = ""
    var myUrl: String = ""
    var fileUrl: String = ""
    lateinit var messegeinput: EditText

    lateinit var binding: ActivityChatBinding
    lateinit var loadingBar:ProgressDialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        loadingBar= ProgressDialog(this)
        recieverId = intent.extras?.getString("visit_user_id")
        name = intent.extras?.getString("visit_user_name")
        messageList = arrayListOf()
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(recieverId.toString())
        RootRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid
        setSupportActionBar(binding.chatToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.textName.setText(name)
        messegeinput = binding.inputMessage
        Toast.makeText(this, recieverId, Toast.LENGTH_SHORT).show()
        messageAdapter = MessageAdapter(applicationContext, messageList)
        val linerLayoutManager = LinearLayoutManager(application.applicationContext)
        linerLayoutManager.stackFromEnd = true
        binding.recyclerview.layoutManager = linerLayoutManager
        binding.recyclerview.setHasFixedSize(true)
        //messageAdapter!!.setText(messageList)
        binding.recyclerview.adapter = messageAdapter
        binding.recyclerview.scrollToPosition(messageList.size - 1)
        messageAdapter?.notifyDataSetChanged()
        UserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val image: String = snapshot.child("profileImage").value.toString()
                // Toast.makeText(baseContext, image, Toast.LENGTH_SHORT).show()
                Glide.with(baseContext).load(image)
                    .placeholder(R.drawable.avatar)
                    .into(binding.imageview)
                val state: String =
                    snapshot.child("userState").child("state").getValue()
                        .toString()
                val date: String =
                    snapshot.child("userState").child("date").getValue()
                        .toString()
                val time: String =
                    snapshot.child("userState").child("time").getValue()
                        .toString()
                if (state.equals("online")) {
                    binding.textLastSeen.setText("online")
                } else if (state.equals("offline")) {
                    binding.textLastSeen.setText("Last seen:" + date + " " + time)
                } else {
                    binding.textLastSeen.setText("offline")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        binding.btnSend.setOnClickListener {
            SendMessage()
        }
        binding.btnSendFiles.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Select the file")
            builder.setItems(R.array.choices, DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> {
                        checker = "image"
                        val intent = Intent()
                        intent.setAction(Intent.ACTION_GET_CONTENT)
                        intent.setType("image/*")
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), 438)

                    }
                    1 -> {
                        checker="pdf"
                    }
                    2 -> {
                      checker="docx"
                    }
                }
            })
            builder.create()
            builder.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun SendMessage() {

        val savecurrentTime: String
        val savecurrentDate: String

        val calendar: Calendar = Calendar.getInstance()
        val currentDate: SimpleDateFormat = SimpleDateFormat("MMM dd,yyyy")
        savecurrentDate = currentDate.format(calendar.time)
        val currentTime: SimpleDateFormat = SimpleDateFormat("hh:mm a")
        savecurrentTime = currentTime.format(calendar.time)

        val messsage: String = messegeinput.text.toString()
        if (TextUtils.isEmpty(messsage)) {
            Toast.makeText(this, "first write your message", Toast.LENGTH_SHORT).show()
        } else {
            val messageSenderRef: String = "Messages/" + currentUserId + "/" + recieverId
            val messageRecieverRef: String = "Messages/" + recieverId + "/" + currentUserId
            val userMessageKeyRef: DatabaseReference =
                RootRef.child("Messages").child(currentUserId.toString())
                    .child(recieverId.toString()).push()
            val messagePushId: String = userMessageKeyRef.key.toString()

            val messageTextBody: HashMap<String, String> = HashMap()
            messageTextBody.put("message", messsage)
            messageTextBody.put("type", "text")
            messageTextBody.put("from", currentUserId.toString())
            messageTextBody.put("messageID", messagePushId)
            messageTextBody.put("time", savecurrentTime)
            messageTextBody.put("date", savecurrentDate)

            val messageBodyDetail: HashMap<String, HashMap<String, String>> = HashMap()
            messageBodyDetail.put(
                messageSenderRef + "/" + messagePushId,
                messageTextBody
            )
            messageBodyDetail.put(
                messageRecieverRef + "/" + messagePushId,
                messageTextBody
            )
            RootRef.updateChildren(messageBodyDetail as Map<String, Any>)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Succcessfull", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this, "fails", Toast.LENGTH_SHORT).show()
                    }
                    messegeinput.setText("")
                }

        }
    }


    override fun onStart() {
        Log.d("SSSS","starting")
        super.onStart()
        // messageList.clear()
        RootRef.child("Messages").child(recieverId.toString()).child(currentUserId.toString())
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message: Messages? = snapshot.getValue(Messages::class.java)
                    if (message != null) {
                        messageList.add(message)
                        messageAdapter?.notifyDataSetChanged()
                    }
                    binding.recyclerview.scrollToPosition(messageList.size - 1)

                    for (item: Messages in messageList) {
                        Log.d("GAT", item.message.toString())
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
//        RootRef.child("Messages").child(currentUserId.toString()).child(recieverId.toString())
//            .addValueEventListener(object:ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val message: Messages? = snapshot.getValue(Messages::class.java)
//                    if (message != null) {
//                        messageList.add(message)
//                    }
//                    messageAdapter?.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//            })
    }

    override fun onDestroy() {
        super.onDestroy()
        recieverId = null

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 438 && resultCode == RESULT_OK) {

            loadingBar.setTitle("Sending file")
            loadingBar.setMessage("Please wait file is sending...")
            loadingBar.setCanceledOnTouchOutside(false)
            loadingBar.show()
            data?.let {
                val fileUrl = it.data

                if (!checker.equals("image")) {

                } else if (checker.equals("image")) {
                    val storageReference: StorageReference =
                        FirebaseStorage.getInstance().getReference().child("Image Files")
                    val messageSenderRef: String = "Messages/" + currentUserId + "/" + recieverId
                    val messageRecieverRef: String = "Messages/" + recieverId + "/" + currentUserId
                    val userMessageKeyRef: DatabaseReference =
                        RootRef.child("Messages").child(currentUserId.toString())
                            .child(recieverId.toString()).push()
                    val messagePushId: String = userMessageKeyRef.key.toString()
                    val filePath: StorageReference =
                        storageReference.child(messagePushId + "." + "jpg")
                    val uploadTask = filePath.putFile(fileUrl!!)
                    uploadTask.addOnSuccessListener {

                        val result: Task<Uri> = it.storage.downloadUrl
                        result.addOnSuccessListener {
                            val savecurrentTime: String
                            val savecurrentDate: String

                            val calendar: Calendar = Calendar.getInstance()
                            val currentDate: SimpleDateFormat = SimpleDateFormat("MMM dd,yyyy")
                            savecurrentDate = currentDate.format(calendar.time)
                            val currentTime: SimpleDateFormat = SimpleDateFormat("hh:mm a")
                            savecurrentTime = currentTime.format(calendar.time)
                            val imageUrl: String = it.toString()


                            val messageTextBody: HashMap<String, String> = HashMap()
                            messageTextBody.put("message", imageUrl)
                            messageTextBody.put("type","image")
                            messageTextBody.put("from", currentUserId.toString())
                            messageTextBody.put("messageID", messagePushId)
                            messageTextBody.put("time", savecurrentTime)
                            messageTextBody.put("date", savecurrentDate)

                            val messageBodyDetail: HashMap<String, HashMap<String, String>> = HashMap()
                            messageBodyDetail.put(
                                messageSenderRef + "/" + messagePushId,
                                messageTextBody
                            )
                            messageBodyDetail.put(
                                messageRecieverRef + "/" + messagePushId,
                                messageTextBody
                            )
                            RootRef.updateChildren(messageBodyDetail as Map<String, Any>)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        loadingBar.dismiss()
                                        Toast.makeText(this, "Succcessfull", Toast.LENGTH_SHORT).show()

                                    } else {
                                        Toast.makeText(this, "fails", Toast.LENGTH_SHORT).show()
                                    }
                                    messegeinput.setText("")
                                }

                        }
                    }
                        .addOnFailureListener {
                                  loadingBar.dismiss()
                            Toast.makeText(this , "error occured", Toast.LENGTH_SHORT).show()
                        }

                }
            }
        }

    }

}