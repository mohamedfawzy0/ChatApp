package com.firstchatapp.activities.splash_activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.firstchatapp.R
import com.firstchatapp.activities.BaseActivity
import com.firstchatapp.activities.users_activity.UsersActivity
import com.firstchatapp.activities.verification_activity.VerificationActivity
import com.firstchatapp.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var handler: Handler
    private lateinit var firebase:FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        initView()
    }

    private fun initView() {
//        firebase=FirebaseAuth.getInstance().currentUser!!
//        Log.e("Userrs",getUserUid().toString()+" ")
//        Log.e("USerData",getUserModel()?.uid.toString()+"")
        handler=Handler()
        handler.postDelayed({
            if (!getUserUid().isNullOrEmpty()){
                if (getUserUid()!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    val intent=Intent(this@SplashActivity, UsersActivity::class.java)
                    startActivity(intent)
                    finish()
            }
            }else{
                val intent=Intent(this@SplashActivity,VerificationActivity::class.java)
                startActivity(intent)
                finish()
            }

        },1000)
    }
}