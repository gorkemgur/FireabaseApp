package com.sample.firebaseapp.chat.viewholder


import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.ui.profile.ProfileActivity

class MessageListSenderViewHolder(var binding: LayoutMessageSenderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName

        binding.userNameTextView.setOnClickListener {

            val intent = Intent(binding.root.context, ProfileActivity::class.java)
            intent.putExtra("userId",model?.userId)
            binding.root.context.startActivity(intent)
        }
    }



}