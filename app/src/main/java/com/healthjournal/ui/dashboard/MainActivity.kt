package com.healthjournal.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.healthjournal.R
import com.healthjournal.data.ResultData
import com.healthjournal.databinding.ActivityMainBinding
import com.healthjournal.ui.journal.input.JournalInputActivity
import com.healthjournal.ui.login.LoginActivity
import com.healthjournal.ui.profile.ProfileActivity
import com.healthjournal.ui.recommendation.RecommendationActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: FirebaseAuth
    private lateinit var mainAdapter: MainAdapter
    private val healthDataList: MutableList<ResultData> = mutableListOf()
    private val database = Firebase.database
    private var dailyReport = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()

        userCheck()
        setupListener()
        populateHistory()
        navigationBottomBar()
    }

    private fun navigationBottomBar(){
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_add -> addJournal()
                R.id.nav_home -> home()
                R.id.nav_profile -> profile()
            }
            true
        }
    }

    private fun getWeekCount() {
        val today = Calendar.getInstance()
        val userID = user.currentUser!!.uid
        var count = 0

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        today.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = dateFormat.format(today.time)

        val endDate = today.clone() as Calendar
        endDate.add(Calendar.DAY_OF_WEEK, 6)
        val endDateString = dateFormat.format(endDate.time)

        database.getReference("users").child(userID).child("journal")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    count = task.result.children.count { snapshot ->
                        val date = snapshot.child("date").value.toString()
                        date >= startDate && date <= endDateString
                    }
                    Log.d("debug", "Entries found: $count for week: $startDate - $endDateString")
                    binding.tvDailyCounter.text = "$count/7"
                } else {
                    Log.e("error", task.exception?.message.toString())
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun addJournal(){
        if (dailyReport != false){
            Toast.makeText(this, "Daily Report Already Created", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(this, JournalInputActivity::class.java))
        }
    }

    private fun home(){
    }

    private fun profile(){
        intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }


    private fun setupListener(){
        binding.btnRecomendation.setOnClickListener {
            startActivity(Intent(this, RecommendationActivity::class.java))
            finish()
        }
        binding.btnInputData.setOnClickListener {
            startActivity(Intent(this, JournalInputActivity::class.java))
            finish()
        }
    }

    private fun dailycheck() {
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(android.icu.util.Calendar.getInstance().time).toString()
        val userID = user.currentUser!!.uid
        database.getReference("users").child(userID).child("journal").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.children.forEach {
                        if (it.child("date").value.toString() == today) {
                            dailyReport = true
                            switchLayout()
                            populateTodayReport()
                        }
                    }
                } else {
                    Log.d("error", task.exception!!.message.toString())
                    Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun switchLayout(){
        if (binding.clDailyReport1.visibility == View.VISIBLE){
            binding.clDailyReport1.visibility = View.INVISIBLE
            binding.clDailyReport2.visibility = View.VISIBLE
            binding.btnRecomendation.visibility = View.VISIBLE
            binding.ivCuboidIndicator1Green.visibility = View.VISIBLE
            binding.ivCuboidIndicator2Green.visibility = View.VISIBLE
            binding.ivCuboidIndicator1Orange.visibility = View.INVISIBLE
            binding.ivCuboidIndicator2Orange.visibility = View.INVISIBLE
        } else {
            binding.clDailyReport1.visibility = View.VISIBLE
            binding.clDailyReport2.visibility = View.INVISIBLE
            binding.btnRecomendation.visibility = View.GONE
            binding.ivCuboidIndicator1Green.visibility = View.INVISIBLE
            binding.ivCuboidIndicator2Green.visibility = View.INVISIBLE
            binding.ivCuboidIndicator1Orange.visibility = View.VISIBLE
            binding.ivCuboidIndicator2Orange.visibility = View.VISIBLE
        }
    }

    private fun userCheck() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Please Login to an account", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
            dailycheck()
            getWeekCount()
        }
    }


    private fun populateHistory() {
        val userID = user.currentUser!!.uid
        mainAdapter = MainAdapter(healthDataList)

        binding.rvHealthHistory.apply {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        database.getReference("users").child(userID).child("journal").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    healthDataList.clear()
                    task.result.children.forEach { snapshot ->
                        val journalID = snapshot.child("date").value.toString()
                        val bloodSugar = snapshot.child("bloodSugar").value.toString().toFloatOrNull() ?: 0f
                        val diastolicBP = snapshot.child("bloodPressureDIA").value.toString().toIntOrNull() ?: 0
                        val systolicBP = snapshot.child("bloodPressureSYS").value.toString().toIntOrNull() ?: 0
                        val BMI = snapshot.child("bmi").value.toString().toFloatOrNull() ?: 0f
                        val date = snapshot.child("date").value.toString()

                        val resultData = ResultData(journalID, bloodSugar, diastolicBP, systolicBP, BMI, date)
                        healthDataList.add(resultData)
                    }
                    mainAdapter.notifyDataSetChanged()
                } else {
                    Log.d("error", task.exception?.message.toString())
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun populateTodayReport(){
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(android.icu.util.Calendar.getInstance().time).toString()
        val userID = user.currentUser!!.uid
        database.getReference("users").child(userID).child("journal").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.children.forEach {
                    if (it.child("date").value.toString() == today) {
                        binding.tvBloodsugarLevel.text = it.child("bloodSugar").value.toString()+" mg/dL"
                        binding.tvBloodsugarDesc.text = it.child("recommendation").child("bloodSugarAnalysis").value.toString()
                        binding.tvBloodpressureLevel.text = it.child("bloodPressureDIA").value.toString()+"/"+it.child("bloodPressureSYS").value.toString()+" mm Hg"
                        binding.tvBloodpressureDesc.text = it.child("recommendation").child("bloodPressureAnalysis").value.toString()
                        binding.tvBmiLevel.text = it.child("BMI").value.toString() +" BMI"
                        binding.tvBmiDesc.text = it.child("recommendation").child("BMIAnalysis").value.toString()
                    }
                }
            } else {
                Log.d("error", task.exception!!.message.toString())
                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}