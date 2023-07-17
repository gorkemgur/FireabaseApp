package com.sample.firebaseapp.chat.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.ProfilePage
import com.sample.firebaseapp.R
import com.sample.firebaseapp.chat.ui.GroupChatActivity
import com.sample.firebaseapp.chat.viewholder.MessageListReceiverViewHolder
import com.sample.firebaseapp.chat.viewholder.MessageListSenderViewHolder
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.MessageModel

class MessageListAdapter(
    private var items: ArrayList<MessageModel>?,
    private val currentUserId: String?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messageViewType: MessageDetailEnum = MessageDetailEnum.SENDER
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            MessageDetailEnum.RECEIVER.ordinal -> {
                MessageListReceiverViewHolder(
                    LayoutMessageReceiverBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,

                        false
                    )
                )
            }
            else -> {
                MessageListSenderViewHolder(
                    LayoutMessageSenderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,

                        false
                    )
                )
            }
        }
    }

    fun updateData(list: ArrayList<MessageModel>?) {
        items = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = items?.get(position)

        when (holder) {
            is MessageListReceiverViewHolder -> {
                holder.bind(message)
                //Deleting a message
                holder.binding.messageTextContainer.setOnLongClickListener {
                    deleteMessage(message)
                    true
                }
                //Navigating profile details
                holder.binding.userNameTextView.setOnClickListener(){
                    var isOwner=false
                    FirebaseHelper.getCurrentUserModel { userModel -> it
                        if (holder.binding.userNameTextView.text==userModel?.name){
                            isOwner=true
                        }
                        val intent= Intent(holder.itemView.context,ProfilePage::class.java).apply {
                            putExtra("username",holder.binding.userNameTextView.text)
                            putExtra("isOwner",isOwner)
                        }
                        holder.itemView.context.startActivity(intent)
                    }

                }
            }
            is MessageListSenderViewHolder -> {
                holder.bind(message)
                //Deleting a message
                holder.binding.messageTextContainer.setOnLongClickListener {
                    deleteMessage(message)
                    true
                }
                //Navigating profile details
                holder.binding.userNameTextView.setOnClickListener(){
                    var isOwner=false
                    FirebaseHelper.getCurrentUserModel { userModel -> it
                        if (holder.binding.userNameTextView.text==userModel?.name){
                            isOwner=true
                        }
                        val intent= Intent(holder.itemView.context,ProfilePage::class.java).apply {
                            putExtra("username",holder.binding.userNameTextView.text)
                            putExtra("isOwner",isOwner)
                        }
                        holder.itemView.context.startActivity(intent)
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }


    override fun getItemViewType(position: Int): Int {
        val message = items?.get(position)
        messageViewType = if (message?.userId == currentUserId) {
            MessageDetailEnum.SENDER
        } else {
            MessageDetailEnum.RECEIVER
        }
        return messageViewType.ordinal
    }
    private fun deleteMessage(message: MessageModel?) {
        val database =Firebase.database
        val myRef=database.getReference("GroupChats")

        if (message != null) {
            myRef.child(message.uniqueKey).child("message").setValue("Mesaj Silindi")
            notifyDataSetChanged()
        }
    }

}


enum class MessageDetailEnum {
    RECEIVER,
    SENDER,
}