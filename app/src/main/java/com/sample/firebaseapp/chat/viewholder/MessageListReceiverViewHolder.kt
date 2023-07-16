package com.sample.firebaseapp.chat.viewholder

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.ui.profile.OtherUsersProfileActivity

class MessageListReceiverViewHolder(var binding: LayoutMessageReceiverBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName



        binding.userNameTextView.setOnClickListener {
            val intent = Intent(binding.root.context, OtherUsersProfileActivity::class.java)
            intent.putExtra("userEmail",model?.email)
            binding.root.context.startActivity(intent)
        }

    }


}