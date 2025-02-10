package com.healthjournal.ui.journal.detail

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
import com.healthjournal.data.ResultData
import com.healthjournal.databinding.ActivityDetailJournalBinding

class DetailJournalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailJournalBinding
    private lateinit var user: FirebaseAuth
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()
        setupListener()
        populateDetailJournal()
    }

    private fun countGoals(taskList: List<Map<String, Any>>): String {
        var completedGoals = 0
        val totalGoals = taskList.size

        for (task in taskList) {
            val isCompleted = task["completed"] as? Boolean ?: false
            if (isCompleted) {
                completedGoals++
            }
        }

        return when {
            totalGoals == 0 -> "No goals available"
            completedGoals == 0 -> "No goals completed"
            else -> "$completedGoals/$totalGoals goals completed"
        }
    }
    private fun populateDetailJournal() {
        val userID = user.currentUser?.uid
        val journalID = intent.getStringExtra("JOURNAL_KEY")

        if (userID != null && journalID != null) {
            database.getReference("users").child(userID).child("journal").child(journalID).get()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        if (result.exists()) {
                            val bmiValue = result.child("BMI").value.toString().toFloatOrNull() ?: 0f
                            Log.d("debug", bmiValue.toString())

                            binding.tvBloodsugarLevel2.text = "${result.child("bloodSugar").value ?: "N/A"} mg/dL"
                            binding.tvBloodsugarDesc.text = result.child("recommendation").child("bloodSugarAnalysis").value?.toString() ?: "No data"
                            binding.tvBloodpressureLevel2.text = "${result.child("bloodPressureDIA").value ?: "N/A"}/${result.child("bloodPressureSYS").value ?: "N/A"} mm Hg"
                            binding.tvBloodpressureDesc.text = result.child("recommendation").child("bloodPressureAnalysis").value?.toString() ?: "No data"
                            binding.tvBMILevel2.text = String.format("%.2f BMI", bmiValue)
                            binding.tvBMIDesc.text = result.child("recommendation").child("BMIAnalysis").value?.toString() ?: "No data"
                            binding.tvJournalNote.text = result.child("note").value?.toString() ?: "No notes available"

                            val taskListSnapshot = result.child("recommendation").child("tasks")
                            Log.d("debug", taskListSnapshot.toString())
                            val taskList = mutableListOf<Map<String, Any>>()
                            if (taskListSnapshot.exists()) {
                                for (taskSnapshot in taskListSnapshot.children) {
                                    val taskMap = taskSnapshot.value as? Map<String, Any>
                                    if (taskMap != null) {
                                        taskList.add(taskMap)
                                    }
                                }
                            }

                            binding.tvGoals2.text = countGoals(taskList)
                        } else {
                            Toast.makeText(this, "No journal entry found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, task.exception?.message ?: "Error fetching data", Toast.LENGTH_SHORT).show()
                        Log.d("error", task.exception?.message.toString())
                    }
                }
        }
    }


    private fun deleteHistory() {
        val userID = user.currentUser?.uid
        val journalDate = intent.getStringExtra("JOURNAL_DATE")
        if (userID != null && journalDate != null) {
            database.getReference("users").child(userID).child("journal").child(journalDate)
                .removeValue()
                .addOnCompleteListener(DetailJournalActivity()) {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "History deleted successfully", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    } else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun setupListener() {
        binding.btnDeleteHistory.setOnClickListener() {
            deleteHistory()
        }
    }
}