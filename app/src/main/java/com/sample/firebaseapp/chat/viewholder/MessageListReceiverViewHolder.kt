package com.sample.firebaseapp.chat.viewholder


import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.model.MessageModel


class MessageListReceiverViewHolder(var binding: LayoutMessageReceiverBinding,private val onUserNameClickListener: OnUserNameClickListener): RecyclerView.ViewHolder(binding.root) {


    interface OnUserNameClickListener {
        fun onUserNameClick(userId: String)
    }

    fun bind(model: MessageModel?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName

        binding.userNameTextView.setOnClickListener {
            val userId = model?.userId ?: ""
            onUserNameClickListener.onUserNameClick(userId)
        }
    }

}