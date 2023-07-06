package com.sample.firebaseapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.dashboard.DashBoardActivity
import com.sample.firebaseapp.databinding.ActivityRegisterBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.ui.common.BaseActivity
import com.sample.firebaseapp.ui.login.LoginActivity

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        checkCurrentUser()

        binding.apply {
            registerButton.setOnClickListener {
                showLoadingProgressBar("Lütfen Bekleyin")
                viewModel.setEmail(emailEditText.text?.toString()?.trim())
                viewModel.setPassword(passwordEditText.text?.toString()?.trim())
                viewModel.setName(nameEditText.text?.toString()?.trim())
                viewModel.setSurname(surnameEditText.text?.toString()?.trim())
                viewModel.register(requestListener = object : RequestListener {
                    override fun onSuccess() {
                        dismissProgressBar()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Kayıt Başarılı ${nameEditText.text?.toString()?.trim()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailed(e: Exception) {
                        dismissProgressBar()
                        Toast.makeText(
                            this@RegisterActivity,
                            e.localizedMessage,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
                emailEditText.text?.clear()
                nameEditText.text?.clear()
                surnameEditText.text?.clear()
                passwordEditText.text?.clear()
            }


            loginButton.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.putExtra("userEmail", viewModel.getEmail())
                startActivity(intent)
            }
        }
    }

    private fun checkCurrentUser() {
        showLoadingProgressBar("Lütfen Bekleyin")
        FirebaseHelper.getCurrentUserModel { userModel ->
            dismissProgressBar()
            if (userModel != null) {
                finish()
                val intent = Intent(this@RegisterActivity, DashBoardActivity::class.java)
                intent.putExtra("userName", userModel.name.toString())
                intent.putExtra("userEmail", viewModel.getEmail())
                startActivity(intent)
            } else {
                binding.containerView.visibility = View.VISIBLE
            }
        }
    }

}