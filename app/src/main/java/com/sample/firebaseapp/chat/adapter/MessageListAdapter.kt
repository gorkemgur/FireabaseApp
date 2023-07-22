package com.sample.firebaseapp.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.chat.viewholder.MessageListReceiverViewHolder
import com.sample.firebaseapp.chat.viewholder.MessageListSenderViewHolder
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel

interface OnMessageLongClickListener {
    fun onMessageLongClick(message: MessageModel)
}

class MessageListAdapter(
    private var items: ArrayList<MessageModel>?,
    private val currentUserId: String?,
    private val onMessageLongClickListener: OnMessageLongClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                        false,
                    ), onMessageLongClickListener
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
        }

        if (holder is MessageListSenderViewHolder) {
            holder.bind(items?.get(position))
        }

        holder.itemView.setOnLongClickListener {
            val message = items?.get(position)
            message?.let {
                if (message.userId == currentUserId) {
                    onMessageLongClickListener.onMessageLongClick(message)
                    message.isDeleted = true
                    notifyItemChanged(position)
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        val message = items?.get(position)
        return if (message?.userId == currentUserId) {
            MessageDetailEnum.SENDER.ordinal
        } else {
            MessageDetailEnum.RECEIVER.ordinal
        }
    }
}

enum class MessageDetailEnum {
    RECEIVER,
    SENDER,
}