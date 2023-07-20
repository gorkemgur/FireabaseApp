package com.sample.firebaseapp.chat.viewholder


import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel


class MessageListSenderViewHolder(var binding: LayoutMessageSenderBinding,private val onUserNameClickListener: MessageListReceiverViewHolder.OnUserNameClickListener) :
    RecyclerView.ViewHolder(binding.root) {



    fun bind(model: MessageModel?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName

        binding.userNameTextView.setOnClickListener {
            val userId = model?.userId ?: ""
            onUserNameClickListener.onUserNameClick(userId)
        }
    }
}