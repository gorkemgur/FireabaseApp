package com.sample.firebaseapp.chat.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.onClickProfile

class MessageListReceiverViewHolder(private var binding: LayoutMessageReceiverBinding, private var listener : onClickProfile): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName
    }

    init {
        binding.userNameTextView.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }

}