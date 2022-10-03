package com.firstchatapp.activities.chat_activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firstchatapp.R
import com.firstchatapp.activities.users_activity.UsersActivity
import com.firstchatapp.adapter.ChatAdapter
import com.firstchatapp.adapter.UserAdapter
import com.firstchatapp.databinding.ActivityChatBinding
import com.firstchatapp.models.Message
import com.firstchatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Calendar
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
//    var storage: StorageReference? = null
    var adapter: ChatAdapter? = null
    private var userId: String? = null
    private var myId: String? = null
    var messageList: ArrayList<Message>? = null
    var userImage: String? = null
    var userName: String? = null
    var myImage: String? = null
    var myRoom: String? = null
    var userRoom: String? = null
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        getDataFromIntent()
        initView()
    }

    private fun getDataFromIntent() {
        val intent = getIntent()
        userId = intent.getStringExtra("userId")
        myImage = intent.getStringExtra("myImage")
        userImage = intent.getStringExtra("userImage")
        userName = intent.getStringExtra("userName")
    }

    private fun initView() {
        dialog = ProgressDialog(this@ChatActivity)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        messageList = ArrayList<Message>()

        val layoutManager = LinearLayoutManager(this@ChatActivity)
        binding.llRecycler.layoutManager = layoutManager

        binding.tvUserName.text = userName
        if (userImage == "") {
            binding.imgProfile.setImageResource(R.drawable.user_img)
        } else {
            Glide.with(this@ChatActivity).load(userImage)
                .placeholder(R.drawable.user_img).into(binding.imgProfile)
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        myId = firebaseUser!!.uid
        reference = FirebaseDatabase.getInstance().getReference("chat")
//        storage = FirebaseStorage.getInstance().getReference("chat")

//        reference!!.child("users").child(userId!!)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val user = snapshot.getValue(User::class.java)
////                    userImage = user!!.profileImage
//                    binding.tvUserName.text = user!!.name
//                    if (user.profileImage == "") {
//                        binding.imgProfile.setImageResource(R.drawable.user_img)
//                    } else {
//                        Glide.with(this@ChatActivity).load(user.profileImage)
//                            .placeholder(R.drawable.user_img).into(binding.imgProfile)
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })
//        child(FirebaseAuth.getInstance().uid!!).child("Presence")

        reference!!.child(userId!!).child("Presence")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (status == "offline") {
                            binding.status.visibility = View.GONE
                        } else {
                            binding.status.visibility = View.VISIBLE
                            binding.status.setText(status)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        myRoom = myId + userId
        userRoom = userId + myId

        adapter = ChatAdapter(
            this@ChatActivity,
            messageList!!,
            userImage!!,
            myImage!!,
            myRoom!!,
            userRoom!!
        )
        binding.llRecycler.layoutManager = LinearLayoutManager(this@ChatActivity)
        binding.llRecycler.adapter = adapter

        reference!!.child("chat")
            .child(myRoom!!)
            .child(",message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList!!.clear()

                    for (snapshot1 in snapshot.children) {
                        val message: Message? = snapshot1.getValue(Message::class.java)
                        message!!.messageId = snapshot1.key
                        messageList!!.add(message)
                    }
                    adapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        binding.btnSendMessage.setOnClickListener {
            val messageTxt: String = binding.etMessage.text.toString()
            val date = Date()
            val message = Message(messageTxt, myId, date.time)
            binding.etMessage.setText("")
            val randomKey = reference!!.push().key
            val lastMsgObj = HashMap<String, Any>()
            lastMsgObj["lastMsg"] = message.message!!
            lastMsgObj["lastMsgTime"] = date.time

            reference!!.child("chat").child(myRoom!!)
                .updateChildren(lastMsgObj)
            reference!!.child("chat").child(userRoom!!)
                .updateChildren(lastMsgObj)
            reference!!.child("chat").child(myRoom!!)
                .child("messages")
                .child(randomKey!!)
                .setValue(message).addOnSuccessListener {
                    reference!!.child("chat")
                        .child(myRoom!!)
                        .child("message")
                        .child(randomKey)
                        .setValue(message)
                        .addOnSuccessListener { }
                }
        }
        val imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                if (result != null) {
                    val uri = result.data!!.data
                    val calendar = Calendar.getInstance()
                    val fileRef: StorageReference = FirebaseStorage.getInstance().getReference("users").child("chat")
                        .child(calendar.timeInMillis.toString() + "")
                    dialog!!.show()
                    fileRef.putFile(uri!!).addOnCompleteListener { task ->
                        dialog!!.dismiss()
                        if (task.isSuccessful) {
                            fileRef.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = uri.toString()
                                val messageTxt: String = binding.etMessage.text.toString()
                                val date = Date()
                                val message = Message(messageTxt, myId, date.time)
                                message.message = "photo"
                                message.imageUrl = filePath
                                binding.etMessage.setText("")
                                val randomKey = reference!!.push().key
                                val lastMsgobj = HashMap<String, Any>()
                                lastMsgobj["lastMsg"] = message.message!!
                                lastMsgobj["lastTime"] = date.time
                                reference!!.child("chat")
                                    .updateChildren(lastMsgobj)
                                reference!!.child("chat")
                                    .child(userRoom!!)
                                    .updateChildren(lastMsgobj)
                                reference!!.child("chat")
                                    .child(myRoom!!)
                                    .child("messages")
                                    .child(randomKey!!)
                                    .setValue(message).addOnSuccessListener {
                                        reference!!.child("chat")
                                            .child(userRoom!!)
                                            .child("messages")
                                            .child(randomKey)
                                            .setValue(message)
                                            .addOnSuccessListener { }
                                    }
                            }
                        }
                    }
                }
            }
        binding.imgAttach.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }
        val handler = Handler()

        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                reference!!.child(userId!!).child("Presence")
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }

            var userStoppedTyping = Runnable {
                reference!!.child(userId!!).child("Presence")
                    .setValue("Online")
            }

        })
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

//        binding.btnSendMessage.setOnClickListener {
//            var message: String = binding.etMessage.text.toString()
//
//            if (message.isEmpty()) {
//                Toast.makeText(this, "message is empty", Toast.LENGTH_LONG).show()
//                binding.etMessage.setText("")
//            } else {
////                sendMessage(firebaseUser!!.uid, userId!!, message)
//                binding.etMessage.setText("")
//            }
//        }

//        readMessage(firebaseUser!!.uid, userId!!)

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

//    private fun sendMessage(senderId: String, receiverId: String, message: String) {
//        val reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
//
//        var hashMap: HashMap<String, String> = HashMap()
//        hashMap.put("myId", myId)
//        hashMap.put("messageId", messageId)
//        hashMap.put("message", message)
//
//        reference!!.child("Chat").push().setValue(hashMap)
//    }

//    fun readMessage(myId: String, messageId: String) {
//        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")
//
//        reference.addValueEventListener(object : ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                messageList!!.clear()
//                for (dataSnapShot: DataSnapshot in snapshot.children) {
//                    val message = dataSnapShot.getValue(Message::class.java)
//
//                    if (message!!.myId.equals(myId) && message!!.messageId.equals(messageId) ||
//                        message!!.myId.equals(messageId) && message!!.messageId.equals(myId)
//                    ) {
//                        messageList!!.add(message)
//                    }
//                }
//                val chatAdapter = ChatAdapter(this@ChatActivity, messageList!!,userImage!!,myImage!!,myRoom!!,userRoom!!)
//                binding.llRecycler.adapter = chatAdapter
//
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
//            }
//
//        })
//
//    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        reference!!.child(currentId!!).child("Presence").setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        reference!!.child(currentId!!).child("Presence").setValue("offline")
    }
}