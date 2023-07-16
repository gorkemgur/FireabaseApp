package com.sample.firebaseapp.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.OnItemClickListener
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel

class MessageListAdapter(
    private var items: ArrayList<MessageModel>?,
    private val currentUserId: String?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messageViewType: MessageDetailEnum = MessageDetailEnum.SENDER
    private var mListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            MessageDetailEnum.RECEIVER.ordinal -> {
                MessageListReceiverViewHolder(
                    LayoutMessageReceiverBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), mListener!!
                )
            }
            else -> {
                MessageListSenderViewHolder(
                    LayoutMessageSenderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), mListener!!
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
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        mListener = clickListener
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
    SENDER,
}