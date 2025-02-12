package com.healthjournal.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.healthjournal.R
import com.healthjournal.databinding.ActivityProfileBinding
import com.healthjournal.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var user: FirebaseAuth
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance()

        setupListener()
        populateData()
    }


    private fun setupListener() {
        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnUpdateData.setOnClickListener {
            updateData()
        }
    }

    private fun logout(){
        user.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun updateData(){
        val userID = user.currentUser?.uid
        val name = binding.edtName.text.toString()
        val date = binding.edtDate.text.toString()
        val weight = binding.edtInputWeight.text.toString()
        val height = binding.edtInputHeight.text.toString()
        val userRef = database.getReference("users").child(userID!!)
        userRef.child("name").setValue(name)
        userRef.child("date").setValue(date)
        userRef.child("weight").setValue(weight)
        userRef.child("height").setValue(height)
        Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show()
        populateData()
    }

    private fun populateData() {
        val userID = user.currentUser?.uid ?: return
        val userRef = database.getReference("users").child(userID)

        userRef.get().addOnCompleteListener(this) {
            if (it.isSuccessful) {
                binding.edtName.setText(it.result.child("name").value.toString())
                binding.tvMyname.text = it.result.child("name").value.toString()
                binding.tvMyemail.text = it.result.child("email").value.toString()
                binding.edtDate.setText(it.result.child("date").value.toString())
                binding.edtInputHeight.setText(it.result.child("height").value.toString())
                binding.edtInputWeight.setText(it.result.child("weight").value.toString())
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                Log.d("ProfileActivity", it.exception.toString())
            }
        }
    }



}