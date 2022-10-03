package com.firstchatapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firstchatapp.R
import com.firstchatapp.databinding.DeleteLayoutBinding

import com.firstchatapp.databinding.ItemReceiveMessageBinding
import com.firstchatapp.databinding.ItemSendMessageBinding
import com.firstchatapp.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ChatAdapter(
    var context: Context,
    var messages: List<Message>,
    var userImage: String,
    var myImage: String,
    var myRoom: String,
    var userRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var inflater: LayoutInflater

    private val MESSAGE_TYPE_LEFT = 1
    private val MESSAGE_TYPE_RIGHT = 2

    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(parent.context)
        if (viewType == MESSAGE_TYPE_LEFT) {
            val binding: ItemReceiveMessageBinding =
                DataBindingUtil.inflate(inflater, R.layout.item_receive_message, parent, false)
            return HolderReceiveMsg(binding)
        } else {

            val binding: ItemSendMessageBinding =
                DataBindingUtil.inflate(inflater, R.layout.item_send_message, parent, false)
            return HolderSendMsg(binding)
        }
//        else if (viewType == img_left) {
//            val binding: ImageMessageLeftRowBinding =
//                DataBindingUtil.inflate(inflater, R.layout.image_message_left_row, parent, false)
//            return HolderImgLeft(binding)
//        } else {
//            val binding: ImageMessageRightRowBinding =
//                DataBindingUtil.inflate(inflater, R.layout.image_message_right_row, parent, false)
//            return HolderImgRight(binding)
//        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder is HolderReceiveMsg) {
            val holderrecieve: HolderReceiveMsg = holder as HolderReceiveMsg
            if (message.message.equals("photo")) {
                holderrecieve.binding.llImage.visibility = View.VISIBLE
                holderrecieve.binding.llMessage.visibility = View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.place_holder)
                    .into(holderrecieve.binding.image)
            }
            holderrecieve.binding.llImage.visibility = View.GONE
            holderrecieve.binding.llMessage.visibility = View.VISIBLE
            holderrecieve.binding.tvMessage.text = message.message
            Glide.with(context).load(userImage).placeholder(R.drawable.user_img)
                .into(holder.binding.userImage)
            holderrecieve.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)

                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()

                binding.everyOne.setOnClickListener {
                    message.message = "This message is removed"
                    message.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Chat")
                            .child(myRoom)
                            .child("message")
                            .child(it1).setValue(message)
                    }
                    message.messageId.let { it2 ->
                        FirebaseDatabase.getInstance().reference.child("Chat")
                            .child(userRoom)
                            .child("message")
                            .child(it2!!).setValue(message)
                    }
                    dialog.dismiss()
                }
                binding.forMee.setOnClickListener {
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Chat")
                            .child(myRoom)
                            .child("message")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }

        } else if (holder is HolderSendMsg) {
            val holderSend: HolderSendMsg = holder as HolderSendMsg
            if (message.message.equals("photo")) {
                holderSend.binding.llImage.visibility = View.VISIBLE
                holderSend.binding.llMessage.visibility = View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.place_holder)
                    .into(holderSend.binding.image)
            }
            holderSend.binding.llImage.visibility = View.GONE
            holderSend.binding.llMessage.visibility = View.VISIBLE
            holderSend.binding.tvMessage.text = message.message
            Glide.with(context).load(myImage).placeholder(R.drawable.user_img)
                .into(holder.binding.myImage)
            holderSend.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)

                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()

                binding.everyOne.setOnClickListener {
                    message.message = "This message is removed"
                    message.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Chat")
                            .child(myRoom)
                            .child("message")
                            .child(it1).setValue(message)
                    }
                    message.messageId.let { it2 ->
                        FirebaseDatabase.getInstance().reference.child("Chat")
                            .child(userRoom)
                            .child("message")
                            .child(it2!!).setValue(message)
                    }
                    dialog.dismiss()
                }
                binding.forMee.setOnClickListener {
                    message.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("Chat")
                            .child(myRoom)
                            .child("message")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }


        }
//        else if (holder is HolderImgLeft) {
//            val holderImgLeft: HolderImgLeft = holder as HolderImgLeft
//            Glide.with(context).load(Uri.parse(chat.image))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holderImgLeft.binding.imageChat)
//        } else if (holder is HolderImgRight) {
//            val holderImgRight: HolderImgRight = holder as HolderImgRight
//            Glide.with(context).load(Uri.parse(chat.image))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holderImgRight.binding.image)
//        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class HolderReceiveMsg(var binding: ItemReceiveMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HolderSendMsg(var binding: ItemSendMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

//    class HolderImgRight(var binding: ImageMessageRightRowBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    class HolderImgLeft(var binding: ImageMessageLeftRowBinding) :
//        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (messages[position].myId == firebaseUser!!.uid) {
            return this.MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }
}