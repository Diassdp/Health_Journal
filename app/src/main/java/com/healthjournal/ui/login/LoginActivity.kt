package com.healthjournal.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.healthjournal.databinding.ActivityLoginBinding
import com.healthjournal.ui.dashboard.MainActivity
import com.healthjournal.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()
        setuplistener()
    }

    private fun setuplistener() {
        binding.btnLogin.setOnClickListener{
            loginUser()
        }

        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) binding.edtEmail.error = "Email cannot be empty"
            if (password.isEmpty()) binding.edtPassword.error = "Password cannot be empty"
            Toast.makeText(this, "Email & Password cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            user.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity()){ task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    Log.d("error", task.exception!!.message.toString())
                }
            }
        }
    }
}