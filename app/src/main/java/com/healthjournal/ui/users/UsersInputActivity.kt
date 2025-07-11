package com.healthjournal.ui.users

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.healthjournal.databinding.ActivityUsersInputBinding
import com.healthjournal.ui.dashboard.MainActivity
import java.util.Calendar

class UsersInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersInputBinding
    private lateinit var user: FirebaseAuth
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()
        val gender = arrayOf("Male", "Female", "Other")

        val conditionAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, gender)
        binding.dropdownGender.setAdapter(conditionAdapter)

        userCheck()
        setuplistener()
    }

    private fun userCheck(){
        if(user.currentUser != null){
            user.currentUser?.let {
                binding.tvEmail.text = it.email
            }
        }
    }

    private fun setuplistener(){
        binding.btnInput.setOnClickListener{
            inputUserData()
        }

        binding.edtDateOfBirth.setOnClickListener{
            datepicker()
        }
    }

    private fun datepicker(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.edtDateOfBirth.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun inputUserData() {
        val userID = user.currentUser?.uid
        val name = binding.edtName.text.toString()
        val gender = binding.dropdownGender.text.toString()
        val date = binding.edtDateOfBirth.text.toString()
        val weight = binding.edtInputWeight.text.toString()
        val height = binding.edtInputHeigh.text.toString()
        if(userID != null){
            if (name.isNotEmpty() && gender.isNotEmpty() && date.isNotEmpty() && weight.isNotEmpty() && height.isNotEmpty()){
                val data = hashMapOf(
                    "name" to name,
                    "gender" to gender,
                    "date" to date,
                    "height" to height,
                    "weight" to weight)
                database.getReference("users").child(userID).setValue(data).addOnCompleteListener(UsersInputActivity()){
                    if(it.isSuccessful){
                        Toast.makeText(this, "Data Input Success", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@UsersInputActivity, MainActivity::class.java))
                        finish()
                    } else{
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                        Log.d("error", it.exception!!.message.toString())
                    }
                }
            }
        }

    }
}