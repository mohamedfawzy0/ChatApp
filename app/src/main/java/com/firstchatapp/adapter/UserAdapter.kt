package com.firstchatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firstchatapp.R
import com.firstchatapp.activities.chat_activity.ChatActivity
import com.firstchatapp.databinding.ItemUserBinding
import com.firstchatapp.models.User

class UserAdapter(var context: Context,var userList: List<User>,var myImage:String) : RecyclerView.Adapter<UserAdapter.MyHolder>(){
    lateinit var inflater: LayoutInflater
//    var onListItemClick:OnListItemClick?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        context=parent.context
        inflater=LayoutInflater.from(parent.context)
        var binding:ItemUserBinding= DataBindingUtil.inflate(inflater, R.layout.item_user,parent,false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val user=userList[position]
        holder.binding.userName.text=user.name
        Glide.with(context).load(user.profileImage).placeholder(R.drawable.user_img).into(holder.binding.userImage)

        holder.itemView.setOnClickListener {
            val intent=Intent(context,ChatActivity::class.java)
            intent.putExtra("userId",user.uid)
            intent.putExtra("myImage",myImage)
            intent.putExtra("userImage",user.profileImage)
            intent.putExtra("userName",user.name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class MyHolder(binding:ItemUserBinding) :RecyclerView.ViewHolder(binding.root) {
        var binding:ItemUserBinding=binding
    }
}