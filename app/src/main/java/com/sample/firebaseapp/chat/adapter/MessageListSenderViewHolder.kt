package com.sample.firebaseapp.chat.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sample.firebaseapp.R
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel

class MessageListSenderViewHolder(var binding: LayoutMessageSenderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?, userImage: String?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName

        if (!userImage.isNullOrEmpty()) {
            // Load the user's image into the ImageView using your preferred image loading library
            // Example with Glide:
            Glide.with(binding.root)
                .load(userImage)
                .into(binding.profilePhotoImageView)
        } else {
            // Set a placeholder or default image if no user image is available
            binding.profilePhotoImageView.setImageResource(R.drawable.ic_default_profile_photo)
        }
    }
}