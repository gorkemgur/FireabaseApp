package com.sample.firebaseapp.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.chat.ui.GroupChatActivity
import com.sample.firebaseapp.databinding.ActivityDashBoardBinding
import com.sample.firebaseapp.ui.register.RegisterActivity

class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardBinding

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)

        intent?.let {
            if (it.hasExtra("userName")) {
                viewModel.setUserName(it.extras?.getString("userName"))
            }

            if (it.hasExtra("userEmail")) {
                viewModel.setEmail(it.extras?.getString("userEmail"))
            }
        }

        setUI()

        setContentView(binding.root)
    }

    private fun setUI() {
        binding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this@DashBoardActivity, RegisterActivity::class.java))
        }
        binding.chatButton.setOnClickListener {
            finish()
            startActivity(Intent(this@DashBoardActivity, GroupChatActivity::class.java))
        }
        setLoggedInUI()
    }


    private fun setLoggedInUI() {
        binding.logoutButton.visibility = View.VISIBLE
        binding.userNameTextViewValue.text = viewModel.getUserName()
        binding.userEmailTextViewValue.text = viewModel.getEmail()
        binding.logoutButton.isEnabled = true
    }


}