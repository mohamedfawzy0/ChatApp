package com.firstchatapp.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firstchatapp.models.User
import com.google.gson.Gson


open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initViews() {}


    protected fun getUserModel(): User? {
        val preferences: SharedPreferences = getSharedPreferences("user_pref",Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("user_data", "")
        val user = gson.fromJson(json, User::class.java)
        return user
    }


    protected fun setUserModel(userModel: User?) {
        val preferences: SharedPreferences =getSharedPreferences("user_pref",MODE_PRIVATE);
        val editor:SharedPreferences.Editor=preferences.edit()
        val gson = Gson()
        val json = gson.toJson(userModel)
        editor.putString("user_data",json)
        editor.apply()
        editor.commit()
    }
    protected fun getUserUid(): String? {
        val preferences: SharedPreferences = getSharedPreferences("user_pref",Context.MODE_PRIVATE)
        val uid = preferences.getString("uid", "")
        return uid


    }
    protected fun getUserName(): String? {
        val preferences: SharedPreferences = getSharedPreferences("user_pref",Context.MODE_PRIVATE)
        val userName = preferences.getString("userName", "")
        return userName
    }
    protected fun getUserPhoneNumber(): String? {
        val preferences: SharedPreferences = getSharedPreferences("user_pref",Context.MODE_PRIVATE)
        val phoneNumber = preferences.getString("phoneNumber", "")
        return phoneNumber
    }
    protected fun getUserImage(): String? {
        val preferences: SharedPreferences = getSharedPreferences("user_pref",Context.MODE_PRIVATE)
        val image = preferences.getString("image", "")
        return image
    }
    protected fun setUserId(uid: String) {
        val preferences: SharedPreferences =getSharedPreferences("user_pref",MODE_PRIVATE);
        val editor=preferences.edit()
        editor.putString("uid",uid)
        editor.apply()
        editor.commit()
    }
    protected fun setUserName(userName: String) {
        val preferences: SharedPreferences =getSharedPreferences("user_pref",MODE_PRIVATE);
        val editor=preferences.edit()
        editor.putString("userName",userName)
        editor.apply()
        editor.commit()
    }
    protected fun setUserPhoneNumber(phoneNumber: String) {
        val preferences: SharedPreferences =getSharedPreferences("user_pref",MODE_PRIVATE);
        val editor=preferences.edit()
        editor.putString("phoneNumber",phoneNumber)
        editor.apply()
        editor.commit()
    }
    protected fun setUserImage(image: String) {
        val preferences: SharedPreferences =getSharedPreferences("user_pref",MODE_PRIVATE);
        val editor=preferences.edit()
        editor.putString("image",image)
        editor.apply()
        editor.commit()
    }

}
