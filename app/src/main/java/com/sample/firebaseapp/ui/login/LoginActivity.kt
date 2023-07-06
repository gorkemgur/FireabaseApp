package com.sample.firebaseapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.dashboard.DashBoardActivity
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
                var email: String? = null
                email = it.extras?.getString("userEmail")
                binding.emailEditText.setText(email)
                viewModel.setEmail(email)
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
        setContentView(binding.root)
    }

    private fun setLoggedInUI() {
        finish()
        val intent = Intent(this@LoginActivity, DashBoardActivity::class.java)
        intent.putExtra("userName", viewModel.getUserName())
        intent.putExtra("userEmail", viewModel.getEmail())
        startActivity(intent)
    }
}