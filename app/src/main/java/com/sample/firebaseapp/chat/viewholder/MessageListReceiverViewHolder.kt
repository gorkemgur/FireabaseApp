package com.sample.firebaseapp.chat.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.model.MessageModel

class MessageListReceiverViewHolder(var binding: LayoutMessageReceiverBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?) {
        if (model?.isDeleted == false) {
            binding.messageTextView.text = model.message
        } else {
            binding.messageTextView.text = "Bu Mesaj Silindi"
            binding.messageTextView.setTextColor(binding.root.context.resources.getColor(android.R.color.darker_gray))
            binding.messageTextView.setTypeface(null, android.graphics.Typeface.ITALIC)
        }
        binding.userNameTextView.text = model?.userName
    }

}