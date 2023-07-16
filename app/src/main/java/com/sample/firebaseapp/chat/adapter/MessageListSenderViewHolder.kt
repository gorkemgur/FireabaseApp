package com.sample.firebaseapp.chat.adapter

import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.OnItemClickListener
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel

class MessageListSenderViewHolder(var binding: LayoutMessageSenderBinding, clickListener: OnItemClickListener) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            clickListener.onItemClick(adapterPosition)
        }
    }

    fun bind(model: MessageModel?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName
    }
}