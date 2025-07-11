package com.healthjournal.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.healthjournal.databinding.ActivityRegisterBinding
import com.healthjournal.ui.login.LoginActivity
import com.healthjournal.ui.users.UsersInputActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()
        setuplistener()
    }

    private fun setuplistener(){
        binding.btnRegister.setOnClickListener{
            registerUser()
        }
        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(){
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtPasswordConfirm.text.toString()

        if (email.isEmpty() ||  password.isEmpty() || confirmPassword.isEmpty()){
            if (email.isEmpty()) binding.edtEmail.error = "Email cannot be empty"
            if (password.isEmpty()) binding.edtPassword.error = "Password cannot be empty"
            if (confirmPassword.isEmpty()) binding.edtPasswordConfirm.error = "Confirm Password cannot be empty"
        } else {
            if (password == confirmPassword){
                user.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity()){ task ->
                   if(task.isSuccessful){
                       Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                       startActivity(Intent(this@RegisterActivity,UsersInputActivity::class.java))
                       finish()
                   } else{
                       Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                       Log.d("error", task.exception!!.message.toString())
                   }
                }
            } else {
                binding.edtPasswordConfirm.error = "Password does not match"
            }
        }
    }

}