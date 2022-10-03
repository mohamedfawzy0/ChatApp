package com.firstchatapp.activities.verification_activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.firstchatapp.R
import com.firstchatapp.activities.BaseActivity
import com.firstchatapp.activities.otp_activity.OTPActivity
import com.firstchatapp.databinding.ActivityVerificationBinding

class VerificationActivity : BaseActivity() {
    lateinit var binding: ActivityVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification)
        initView()
    }

    private fun initView() {
//        firebaseUser=FirebaseAuth.getInstance().currentUser!!
//        Log.e("userr",FirebaseAuth.getInstance().currentUser!!.uid.toString()+"")
//        val intent = Intent(this, ContactsActivity::class.java)
//        startActivity(intent)
//        finish()
        supportActionBar?.hide()
        binding.btnContinue.setOnClickListener {
            validateNumber()

        }
    }

    private fun validateNumber() {
        if (binding.etNumber?.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter your mobile Number", Toast.LENGTH_LONG).show()
            binding.etNumber.requestFocus()


        } else {
            val intent = Intent(this@VerificationActivity, OTPActivity::class.java)
            intent.putExtra("phoneNumber", binding.etNumber?.text.toString())
            startActivity(intent)
            finish()
        }
    }
}