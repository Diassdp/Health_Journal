package com.healthjournal.ui.journal.input

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.healthjournal.algorithm.AlgoritmaKesehatan
import com.healthjournal.databinding.ActivityJournalInputBinding
import com.healthjournal.ui.dashboard.MainActivity
import com.healthjournal.data.HealthData
import java.text.SimpleDateFormat
import java.util.Locale

class JournalInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJournalInputBinding
    private lateinit var user: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJournalInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val userId = user.currentUser?.uid
        if (userId != null) {
            database.getReference("users").child(userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.exists()) {
                        binding.edtInputWeight.setText(task.result.child("weight").value.toString())
                        binding.edtHeight.setText(task.result.child("height").value.toString())
                        binding.edtAge.setText(task.result.child("date").value.toString())
                        binding.edtGender.setText(task.result.child("gender").value.toString())
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.d("error", task.exception?.message.toString())
                    }
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupListener()
    }

    private fun setupListener() {
        binding.btnInputData.setOnClickListener {
            submitData()
        }
    }

    private fun calculateBMI(weight: Int, height: Int): Float {
        val heightInMeters = height / 100f
        val BMI = weight / (heightInMeters * heightInMeters)
        return BMI
    }

    fun calculateAge(birthDate: String): Int {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.parse(birthDate)
        val birthCalendar = Calendar.getInstance()
        birthCalendar.time = date
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    private fun submitData() {
        val userId = user.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val journalDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time).toString()
        val bloodPressureSYS = binding.edtInputBloodPressureSYS.text.toString()
        val bloodPressureDIA = binding.edtInputBloodPressureDIA.text.toString()
        val bloodSugar = binding.edtInputBloodSugar.text.toString()
        val weight = binding.edtInputWeight.text.toString().toInt()
        val height = binding.edtHeight.text.toString().toInt()
        val note = binding.edtInputNote.text.toString()
        val age = calculateAge(binding.edtAge.text.toString())
        val gender = binding.edtGender.text.toString()
        Log.d("journalDate", journalDate)
        if (weight != null && bloodPressureSYS.isNotEmpty() && bloodPressureDIA.isNotEmpty() && bloodSugar.isNotEmpty()) {
            database.getReference("users").child(userId).child("height").get().addOnSuccessListener { snapshot ->
                val BMI = calculateBMI(weight, height)
                val healthData = HealthData(bloodSugar.toFloat(),bloodPressureDIA.toInt(),bloodPressureSYS.toInt(), BMI,age,gender)
                val recommendation = AlgoritmaKesehatan().recommendationOfTheDay(healthData)

                val data = hashMapOf(
                    "date" to journalDate,
                    "bloodPressureSYS" to bloodPressureSYS,
                    "bloodPressureDIA" to bloodPressureDIA,
                    "bloodSugar" to bloodSugar,
                    "BMI" to BMI,
                    "note" to note,
                    "recommendation" to recommendation
                )
                database.getReference("users").child(userId).child("journal").push().setValue(data).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Data Input Success", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                            Log.d("error", it.exception?.message.toString())
                        }
                }
            }
        } else {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
        }
    }
}
