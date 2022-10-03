package com.firstchatapp.activities.otp_activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.firstchatapp.R
import com.firstchatapp.activities.BaseActivity
import com.firstchatapp.activities.activity_setupProfile.SetUpProfileActivity
import com.firstchatapp.activities.verification_activity.VerificationActivity
import com.firstchatapp.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OTPActivity : BaseActivity() {
    lateinit var binding: ActivityOtpBinding

    lateinit var phoneNumber: String
    lateinit var verificationIdd: String

    private lateinit var auth: FirebaseAuth
    private var firebaseUser: FirebaseUser?=null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
//    private  var user: User?=null

    var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        getDataFromIntent()
        initView()
    }

    private fun getDataFromIntent() {
        phoneNumber= intent.getStringExtra("phoneNumber").toString()
        binding.tvNumber.text= "Verify $phoneNumber"

    }

    private fun initView() {

        auth = FirebaseAuth.getInstance()
        auth.useAppLanguage()
//        firebaseUser=auth.currentUser!!
//        if (firebaseUser!=null){
//            val intent = Intent(this, ContactsActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
        supportActionBar?.hide()

        binding.btnConfirm.setOnClickListener {
            var code=binding.otpView.text.toString()
            if (!code.isNullOrEmpty()){
                binding.otpView.clearFocus()
                checkValidCode(code)
            }else{
                binding.otpView.requestFocus()
                Toast.makeText(this,"Enter OTP Code",Toast.LENGTH_LONG).show()
            }
        }

        verificationCallbacks()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

//        user=User(firebaseUser.uid,"",phoneNumber,"")
        setUserPhoneNumber(phoneNumber)
    }

    private fun verificationCallbacks() {
        dialog = ProgressDialog(this@OTPActivity)
        dialog!!.setMessage("Sending OTP Code...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                val code = credential.smsCode
                if (code != null) {
                    checkValidCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        Toast.makeText(this@OTPActivity, "Invalid request", Toast.LENGTH_SHORT).show()
                        returnToEnterNumberActivity()

                    }
                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(
                            this@OTPActivity,
                            "The SMS quota for the project has been exceeded",
                            Toast.LENGTH_SHORT
                        ).show()
                        returnToEnterNumberActivity()

                    }
                    else -> {
                        // Show a message and update the UI
                        Toast.makeText(this@OTPActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
                        returnToEnterNumberActivity()
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                verificationIdd = verificationId
                resendToken = token
                dialog!!.dismiss()

                Toast.makeText(
                    this@OTPActivity,
                    "OTP sent to $phoneNumber",
                    Toast.LENGTH_SHORT
                ).show()

                super.onCodeSent(verificationId, resendToken)

            }
        }
    }

    private fun checkValidCode(code:String) {
        var credential = PhoneAuthProvider.getCredential(verificationIdd, code)

        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        dialog = ProgressDialog(this@OTPActivity)
        dialog!!.setMessage("Confirming...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
//                    Log.e(this, "signInWithCredential:success")
//                    Toast.makeText(this, "Authorization Completed 🥳🥳", Toast.LENGTH_SHORT).show()

                    val user = task.result?.user
                    dialog!!.dismiss()
                    val intent = Intent(this, SetUpProfileActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    // Sign in failed, display a message and update the UI
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "The verification code entered was invalid 🥺",Toast.LENGTH_SHORT).show()
                    } else {
                        // Update UI
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
//                    returnToEnterNumberActivity()

                }
            }

    }

    private fun returnToEnterNumberActivity() {
        val intent = Intent(applicationContext, VerificationActivity::class.java)
        startActivity(intent)
        finish()
    }
}