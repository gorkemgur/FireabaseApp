package com.sample.firebaseapp.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sample.firebaseapp.chat.ui.GroupChatActivity
import com.sample.firebaseapp.chat.viewholder.MessageListReceiverViewHolder
import com.sample.firebaseapp.chat.viewholder.MessageListSenderViewHolder
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel

class MessageListAdapter(
    private var items: ArrayList<MessageModel>?,
    private val currentUserId: String?,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messageViewType: MessageDetailEnum = MessageDetailEnum.SENDER
    private var adapter: MessageListAdapter? = null
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
        }.also { viewHolder ->
            adapter = this
        }
    }

    fun updateData(list: ArrayList<MessageModel>?) {
        items = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessageListReceiverViewHolder) {
            holder.bind(items?.get(position))
        }

        if (holder is MessageListSenderViewHolder) {
            holder.bind(items?.get(position))
        }

        holder.itemView.setOnLongClickListener {
            val message = items?.get(position)
            val currentUserId = currentUserId ?: ""
            if (message?.userId == currentUserId) {
                val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
                alertDialogBuilder.setTitle("Mesajı Sil")
                alertDialogBuilder.setMessage("Bu mesajı silmek istediğinize emin misiniz?")
                alertDialogBuilder.setPositiveButton("Evet") { dialog, _ ->

                    deleteMessage(position)
                    items!!.removeAt(position)
                    adapter!!.notifyItemRemoved(position)
                    notifyDataSetChanged()
                    dialog.dismiss()
                }
                alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
            true
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


    fun deleteMessage(position: Int) {

        val message = items?.get(position)
        val currentUserId = currentUserId ?: ""

        if (message?.userId == currentUserId) {
            val messageId = message.messageId
            val databaseReference = Firebase.database.reference
            databaseReference.child("GroupChats").child(messageId!!).removeValue().addOnSuccessListener {
             notifyItemRemoved(position)
             notifyDataSetChanged()
            }.addOnFailureListener {

            }
        }
    }

}

enum class MessageDetailEnum {
    RECEIVER,
    SENDER,
}