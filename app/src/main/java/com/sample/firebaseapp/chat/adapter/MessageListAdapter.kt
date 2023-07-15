package com.sample.firebaseapp.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.chat.viewholder.MessageListReceiverViewHolder
import com.sample.firebaseapp.chat.viewholder.MessageListSenderViewHolder
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel


interface MessageClickListener {
    fun showDeleteConfirmationDialog(message: MessageModel)
}

class MessageListAdapter(
    private var items: ArrayList<MessageModel>?,
    private val currentUserId: String?,
    private val messageClickListener: MessageClickListener
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
        if (holder is MessageListReceiverViewHolder) {
            holder.bind(items?.get(position))
            holder.itemView.setOnLongClickListener(){
                messageClickListener.showDeleteConfirmationDialog(items!!.get(position))
                true

            }
        }

        if (holder is MessageListSenderViewHolder) {
            holder.bind(items?.get(position))
            holder.itemView.setOnLongClickListener {
                messageClickListener.showDeleteConfirmationDialog(items!!.get(position))
                true
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

    fun removeMessage(message: MessageModel) {
        val position = items?.indexOf(message)
        if (position != null && position != -1) {
            items?.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}


enum class MessageDetailEnum {
    RECEIVER,
    SENDER,
}