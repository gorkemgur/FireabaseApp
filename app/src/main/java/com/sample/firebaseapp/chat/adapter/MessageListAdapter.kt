package com.sample.firebaseapp.chat.adapter

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.ui.profile.ProfileActivity

class MessageListAdapter(
    private var items: ArrayList<MessageModel>?,
    private val currentUserId: String?,
    private val deleteListener: (MessageModel) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messageViewType: MessageDetailEnum = MessageDetailEnum.SENDER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MessageDetailEnum.RECEIVER.ordinal -> {
                MessageListReceiverViewHolder(
                    LayoutMessageReceiverBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
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
        if (holder is MessageListReceiverViewHolder) {
            holder.bind(items?.get(position))
            if (items?.get(position)?.isDeleted == true)
                holder.binding.messageTextView.setTextColor(Color.GRAY)
            else
                holder.binding.messageTextView.setTextColor(Color.BLACK)
            holder.binding.userNameTextView.setOnClickListener {
                val it = Intent(holder.binding.root.context, ProfileActivity::class.java)
                it.putExtra("userId", items?.get(position)?.userId)
                holder.binding.root.context.startActivity(it)
            }
        } else if (holder is MessageListSenderViewHolder) {
            holder.itemView.setOnLongClickListener {
                items?.get(position)
                    ?.let { it1 -> showDeleteOption(holder.binding.root, it1, position ) }
                true
            }
            holder.bind(items?.get(position))
            if (items?.get(position)?.isDeleted == true)
                holder.binding.messageTextView.setTextColor(Color.GRAY)
            else
                holder.binding.messageTextView.setTextColor(Color.BLACK)

            holder.binding.userNameTextView.setOnClickListener {
                val it = Intent(holder.binding.root.context, ProfileActivity::class.java)
                it.putExtra("userId", items?.get(position)?.userId)
                holder.binding.root.context.startActivity(it)
            }
        }
    }

    private fun showDeleteOption(view: View, message: MessageModel, position: Int) {
        AlertDialog.Builder(view.context)
            .setTitle("Delete message?")
            .setPositiveButton("Delete") {  _, _ ->
                deleteListener.invoke(items?.get(position)!!)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
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
}

enum class MessageDetailEnum {
    RECEIVER,
    SENDER
}