package com.firstchatapp.activities.users_activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firstchatapp.R
import com.firstchatapp.activities.BaseActivity
import com.firstchatapp.activities.profile_activity.ProfileActivity
import com.firstchatapp.adapter.UserAdapter
import com.firstchatapp.databinding.ActivityUsersBinding
import com.firstchatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UsersActivity : BaseActivity() {
    lateinit var binding: ActivityUsersBinding

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    var usersList: ArrayList<User>? = null
    var userAdapter: UserAdapter? = null
    var dialog: ProgressDialog? = null
    var myImage:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_users)
        initView()
    }

    private fun initView() {

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)
        usersList = ArrayList<User>()

        val layoutManager = LinearLayoutManager(this@UsersActivity)
        binding.recyclerView.layoutManager = layoutManager

        getUserList()

        binding.myImage.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                Toast.makeText(this, "No user Data to show,plz login first..", Toast.LENGTH_LONG)
                    .show()
            } else {
                val intent = Intent(this@UsersActivity, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }


        }
    }


    fun getUserList() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                usersList!!.clear()

                val myimage = getUserImage()
                myImage=myimage

                if (myimage!!.isEmpty()) {
                    binding.myImage.setImageResource(R.drawable.user_img)
                } else {
                    Glide.with(applicationContext).load(myimage!!).placeholder(R.drawable.user_img).into(binding.myImage)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.uid.equals(firebaseUser.uid)) {
                        usersList!!.add(user)
                    }
                }
                userAdapter = UserAdapter(applicationContext, usersList!!,myImage!!)
                binding.recyclerView.adapter = userAdapter


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }

        })

    }

    override fun onResume() {
        super.onResume()
        val currentId=FirebaseAuth.getInstance().uid
        databaseReference.child(currentId!!).child("Presence").setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId=FirebaseAuth.getInstance().uid
        databaseReference.child(currentId!!).child("Presence").setValue("Online")

    }
}