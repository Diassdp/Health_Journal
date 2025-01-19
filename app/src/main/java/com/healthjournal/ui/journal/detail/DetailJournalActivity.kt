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
        val healthData = intent.getSerializableExtra("HEALTH_DATA") as? ResultData

    }

    private fun populateDetailJournal() {
        val userID = user.currentUser?.uid
        val journalDate = intent.getStringExtra("JOURNAL_DATE")
        if (userID != null && journalDate != null) {
            database.getReference("users").child(userID).child("journal").child(journalDate).get()
                .addOnCompleteListener(DetailJournalActivity()) {
                    if (it.isSuccessful) {
                        val data = it.result
                        if (data != null) {
/*
                            binding.tvBloodsugarLevel2.text = data.bloodSugarLevel
                            binding.tvBloodsugarDesc.text = data.bloodSugarDesc
                            binding.tvBloodpressureLevel2.text = data.bloodPressureLevel
                            binding.tvBloodpressureDesc.text = data.bloodPressureDesc
                            binding.tvBMILevel2.text = data.bmiLevel
                            binding.tvBMIDesc.text = data.bmiDesc
                            binding.tvGoals2.text = countGoalsCompleted(data.goalsCompleted)
                            binding.tvJournalNote.text = data.journalNote
*/
                        }
                    } else {
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                        Log.d("error", it.exception!!.message.toString())
                    }
                }
        }
    }

    private fun countGoalsCompleted(goalsCompleted: List<Boolean>): String {
        var count = 0
        for (completed in goalsCompleted) {
            if (completed) {
                count++
            }
        }
        return count.toString()
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