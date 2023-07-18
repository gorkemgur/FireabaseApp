package com.sample.firebaseapp.chat.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.chat.adapter.MessageListener
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel

class MessageListSenderViewHolder(
    var binding: LayoutMessageSenderBinding,
    private val listener: MessageListener?
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?) {
        if (model?.isDeleted == false) {
            binding.root.setOnLongClickListener {
                listener?.onLongClicked(model)
                true
            }
            binding.messageTextView.text = model.message
        } else {
            binding.messageTextView.text = "Bu Mesaj Silindi"
        }
        binding.userNameTextView.text = model?.userName
    }
}