package com.healthjournal.ui.recommendation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.healthjournal.databinding.ActivityRecommendationBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RecommendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationBinding
    private lateinit var user: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: RecommendationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        populateData()
    }

    private fun populateData() {
        val userID = user.currentUser?.uid ?: return
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(android.icu.util.Calendar.getInstance().time)

        database.getReference("users").child(userID).child("journal")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.children.forEach {
                        if (it.child("date").value.toString() == today) {
                            val bmiValue = it.child("BMI").value.toString().toDoubleOrNull() ?: 0.0

                            binding.tvBloodsugarLevel2.text = "${it.child("bloodSugar").value} mg/dL"
                            binding.tvBloodsugarDesc.text = it.child("recommendation").child("bloodSugarAnalysis").value.toString()
                            binding.tvBloodpressureLevel2.text = "${it.child("bloodPressureDIA").value}/${it.child("bloodPressureSYS").value} mm Hg"
                            binding.tvBloodpressureDesc.text = it.child("recommendation").child("bloodPressureAnalysis").value.toString()
                            binding.tvBMILevel2.text = "${bmiValue} BMI"
                            binding.tvBMIDesc.text = it.child("recommendation").child("BMIAnalysis").value.toString()

                            val tasks = it.child("recommendation").child("tasks").value
                            val referencePath = it.key ?: return@forEach
                            populateRecomendation(tasks, userID, referencePath)
                        }
                    }
                } else {
                    Log.e("error", task.exception?.message.toString())
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun populateRecomendation(tasks: Any?, userID: String, journalKey: String) {
        val taskList = mutableListOf<Pair<String, Boolean>>()

        // Handle List format for tasks
        if (tasks is List<*>) {
            tasks.forEach { taskData ->
                if (taskData is Map<*, *> && taskData["task"] != null && taskData["completed"] != null) {
                    val taskDescription = taskData["task"].toString()
                    val isCompleted = taskData["completed"] as? Boolean ?: false
                    taskList.add(Pair(taskDescription, isCompleted))
                }
            }

            // Set up RecyclerView and Adapter
            adapter = RecommendationAdapter(taskList) { position, isChecked ->
                taskList[position] = taskList[position].copy(second = isChecked)

                // Update Firebase
                database.getReference("users")
                    .child(userID)
                    .child("journal")
                    .child(journalKey)
                    .child("recommendation")
                    .child("tasks")
                    .child(position.toString())
                    .child("completed")
                    .setValue(isChecked)
            }

            binding.rvTasks.apply {
                layoutManager = LinearLayoutManager(this@RecommendationActivity)
                adapter = this@RecommendationActivity.adapter
            }
        } else {
            Log.e("error", "Invalid tasks data format")
            Log.e("error", tasks.toString())
        }
    }
}
