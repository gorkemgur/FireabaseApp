package com.sample.firebaseapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.databinding.ActivityLoginBinding
import com.sample.firebaseapp.extension.hideKeyboard
import com.sample.firebaseapp.ui.common.BaseActivity
import com.sample.firebaseapp.ui.register.RegisterActivity

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        intent?.let {
            if (it.hasExtra("userEmail")) {
                binding.userNameTextView.text = it.extras?.getString("userEmail")
            }
            if (it.hasExtra("userName")) {
                val userName = it.extras?.getString("userName")
                viewModel.setUserName(userName)
                setLoggedInUI()
            } else {
                binding.loginContainerView.visibility = View.VISIBLE
            }
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            viewModel.setEmail(binding.emailEditText.text.toString().trim())
            viewModel.setPassword(binding.passwordEditText.text.toString().trim())
            showLoadingProgressBar("Lütfen Bekleyin")
            viewModel.login(requestListener = object : RequestListener {
                override fun onSuccess() {
                    dismissProgressBar()
                    setLoggedInUI()
                }

                override fun onFailed(e: Exception) {
                    dismissProgressBar()
                    Toast.makeText(this@LoginActivity, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            })
            binding.root.hideKeyboard()
        }

        binding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        setContentView(binding.root)
    }

    private fun setLoggedInUI() {
        binding.userNameTextView.visibility = View.VISIBLE
        binding.logoutButton.visibility = View.VISIBLE
        binding.userNameTextView.text = viewModel.getUserName()
        binding.logoutButton.isEnabled = true
        binding.loginContainerView.visibility = View.GONE
    }
}