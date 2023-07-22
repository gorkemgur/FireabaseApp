package com.sample.firebaseapp.chat.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.chat.adapter.OnMessageLongClickListener
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel

class MessageListSenderViewHolder(
    var binding: LayoutMessageSenderBinding,
    private val listener: OnMessageLongClickListener?
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?) {
        if (model?.isDeleted == false) {
            binding.root.setOnLongClickListener {
                listener?.onMessageLongClick(model)
                true
            }
            binding.messageTextView.text = model.message
        } else {
            binding.messageTextView.text = "Bu Mesaj Silindi"
            binding.messageTextView.setTextColor(binding.root.context.resources.getColor(android.R.color.darker_gray))
            binding.messageTextView.setTypeface(null, android.graphics.Typeface.ITALIC)
        }
        binding.userNameTextView.text = model?.userName
    }
}