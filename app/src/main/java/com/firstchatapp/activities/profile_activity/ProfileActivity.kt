package com.firstchatapp.activities.profile_activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.firstchatapp.R
import com.firstchatapp.activities.BaseActivity
import com.firstchatapp.activities.users_activity.UsersActivity
import com.firstchatapp.activities.verification_activity.VerificationActivity
import com.firstchatapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initView()
    }

    private fun initView() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = getUserModel()
                Log.e("userr",user!!.profileImage.toString())

                binding.userName.text = user!!.name

                if (user!!.profileImage=="") {
                    binding.userImage.setImageResource(R.drawable.user_img)
                } else {
                    Glide.with(this@ProfileActivity)
                        .load(user.profileImage)
                        .placeholder(R.drawable.user_img)
                        .into(binding.userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity,error.message,Toast.LENGTH_LONG).show()
            }

        })
        binding.imgBack.setOnClickListener {
            val intent=Intent(this, UsersActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.llLogout.setOnClickListener {
            setUserId("")
            setUserModel(null)
            val intent=Intent(this,VerificationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent=Intent(this, UsersActivity::class.java)
        startActivity(intent)
    }
}