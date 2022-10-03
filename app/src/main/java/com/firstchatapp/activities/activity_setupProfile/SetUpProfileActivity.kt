package com.firstchatapp.activities.activity_setupProfile

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.firstchatapp.R
import com.firstchatapp.activities.BaseActivity
import com.firstchatapp.activities.users_activity.UsersActivity
import com.firstchatapp.databinding.ActivitySetUpProfileBinding
import com.firstchatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SetUpProfileActivity : BaseActivity() {
    lateinit var binding: ActivitySetUpProfileBinding

    lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    lateinit var storage: StorageReference
    lateinit var imageUri: Uri
    lateinit var dialog: ProgressDialog
    var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_up_profile)
        initView()
    }

    private fun initView() {

        database = FirebaseDatabase.getInstance().getReference("users")
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid


        supportActionBar?.hide()

        val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result != null) {
                val uri = result.data!!.data
                imageUri = uri!!
                binding.imgProfile.setImageURI(uri)

            }
        }
        binding.imgProfile.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            imageLauncher.launch(intent)
        }

        binding.btnSetup.setOnClickListener {
            showDialog()
            uploadProfileData()
        }

    }

    private fun navigateToContacts() {
        val intent = Intent(this, UsersActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getFileExtension(muri: Uri): String {
        var resolver: ContentResolver = contentResolver
        var mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(resolver.getType(muri))!!
    }

    private fun uploadProfileData() {
        storage = FirebaseStorage.getInstance().getReference("users/" + auth.currentUser?.uid)
        val fileRef: StorageReference =
            storage.child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))
        fileRef.putFile(imageUri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                val uri = it
                val phone = auth.currentUser!!.phoneNumber
                val name = binding.etName.text.toString()
                val user = User(uid, name, phone, uri.toString())
                setUserModel(user)
                setUserImage(uri.toString())
                setUserName(name)
                Toast.makeText(this, "Image successfully uploaded", Toast.LENGTH_LONG).show()

                if (uid != null) {
                    database.child(uid!!).setValue(user).addOnCompleteListener {
                        if (it.isSuccessful) {
                            setUserId(uid!!)
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_LONG)
                                .show()
                            navigateToContacts()
                        } else {
                            hideDialog()
                            Toast.makeText(this, "Failed to update Profile", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                hideDialog()
            }
        }.addOnFailureListener {
            hideDialog()
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDialog() {
        dialog = ProgressDialog(this)
        dialog.setMessage("Updating...")
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun hideDialog() {
        dialog.dismiss()
    }

}

