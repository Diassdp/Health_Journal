package com.healthjournal.ui.dashboard

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.healthjournal.R
import com.healthjournal.data.ResultData
import com.healthjournal.databinding.ActivityMainBinding
import com.healthjournal.receiver.ReminderReceiver
import com.healthjournal.ui.journal.input.JournalInputActivity
import com.healthjournal.ui.laporan.laporan
import com.healthjournal.ui.login.LoginActivity
import com.healthjournal.ui.profile.ProfileActivity
import com.healthjournal.ui.recommendation.RecommendationActivity
import com.healthjournal.ui.users.UsersInputActivity
import com.healthjournal.utils.NotificationUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: FirebaseAuth
    private lateinit var mainAdapter: MainAdapter
    private lateinit var database: FirebaseDatabase
    private val healthDataList: MutableList<ResultData> = mutableListOf()
    private var dailyReport = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        NotificationUtils.createNotificationChannel(this)
        scheduleHealthReminders(this)
        setContentView(binding.root)
        user = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        userCheck()
        setupListener()
        navigationBottomBar()
    }

    private fun scheduleHealthReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val daysOfWeek = listOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY)

        for (day in daysOfWeek.shuffled().take(3)) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, day)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, day, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }
    }

    private fun navigationBottomBar() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish()
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.ivAdd.setOnClickListener {
            if (dailyReport) {
                Toast.makeText(this, "Laporan harian sudah dibuat", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, JournalInputActivity::class.java))
            }
        }
    }

    private fun getWeekCount() {
        val today = Calendar.getInstance()
        val userID = user.currentUser!!.uid
        var count = 0

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        today.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = today.time

        val endDate = today.clone() as Calendar
        endDate.add(Calendar.DAY_OF_WEEK, 6)
        val endDateDate = endDate.time

        database.getReference("users").child(userID).child("journal").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    count = task.result.children.count { snapshot ->
                        val dateString = snapshot.child("date").value.toString()
                        val entryDate = try {
                            dateFormat.parse(dateString)
                        } catch (e: Exception) {
                            null
                        }
                        entryDate?.let { it in startDate..endDateDate } ?: false
                    }
                    Log.d("debug", "Jumlah entri: $count minggu ini: ${dateFormat.format(startDate)} - ${dateFormat.format(endDateDate)}")
                    binding.tvDailyCounter.text = "$count/7"
                } else {
                    Log.e("error", task.exception?.message.toString())
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupListener() {
        binding.btnRecomendation.setOnClickListener {
            startActivity(Intent(this, RecommendationActivity::class.java))
        }

        binding.btnInputData.setOnClickListener {
            if (dailyReport) {
                Toast.makeText(this, "Laporan harian sudah dibuat", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, JournalInputActivity::class.java))
            }
        }

        binding.btnReport.setOnClickListener {
            val intent = Intent(this, laporan::class.java)
            intent.putExtra("healthDataList", ArrayList(healthDataList))
            startActivity(intent)
        }
    }

    private fun dailycheck() {
        getWeekCount()
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        val userID = user.currentUser!!.uid
        database.getReference("users").child(userID).child("journal").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.children.forEach {
                    if (it.child("date").value.toString() == today) {
                        dailyReport = true
                        switchLayout()
                        val referencePath = it.key ?: return@forEach
                        populateTodayReport(referencePath)
                    }
                }
            } else {
                Log.d("error", task.exception!!.message.toString())
                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun switchLayout() {
        if (binding.clDailyReport1.visibility == View.VISIBLE) {
            binding.clDailyReport1.visibility = View.INVISIBLE
            binding.clDailyReport2.visibility = View.VISIBLE
            binding.btnRecomendation.visibility = View.VISIBLE
            binding.btnInputData.visibility = View.GONE
            binding.ivCuboidIndicator1Green.visibility = View.VISIBLE
            binding.ivCuboidIndicator2Green.visibility = View.VISIBLE
            binding.ivCuboidIndicator1Orange.visibility = View.INVISIBLE
            binding.ivCuboidIndicator2Orange.visibility = View.INVISIBLE
        } else {
            binding.clDailyReport1.visibility = View.VISIBLE
            binding.clDailyReport2.visibility = View.INVISIBLE
            binding.btnRecomendation.visibility = View.GONE
            binding.btnInputData.visibility = View.VISIBLE
            binding.ivCuboidIndicator1Green.visibility = View.INVISIBLE
            binding.ivCuboidIndicator2Green.visibility = View.INVISIBLE
            binding.ivCuboidIndicator1Orange.visibility = View.VISIBLE
            binding.ivCuboidIndicator2Orange.visibility = View.VISIBLE
        }
    }

    private fun userCheck() {
        val firebaseUser = user.currentUser
        if (firebaseUser != null) {
            val userID = firebaseUser.uid
            val userRef = database.getReference("users").child(userID)
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.exists() && task.result.childrenCount > 0) {
                        Toast.makeText(this, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()
                        dailycheck()
                        getWeekCount()
                        populateHistory()
                    } else {
                        Toast.makeText(this, "Data kesehatan belum tersedia. Silakan isi profil terlebih dahulu.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@MainActivity, UsersInputActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Gagal mengambil data pengguna.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun populateHistory() {
        val userID = user.currentUser!!.uid
        mainAdapter = MainAdapter(healthDataList)

        binding.rvHealthHistory.apply {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        val historyRef = database.getReference("users").child(userID).child("journal")

        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                healthDataList.clear()
                snapshot.children.forEach { data ->
                    val journalID = data.key.toString()
                    val bloodSugar = data.child("bloodSugar").value.toString().toFloatOrNull() ?: 0f
                    val diastolicBP = data.child("bloodPressureDIA").value.toString().toIntOrNull() ?: 0
                    val systolicBP = data.child("bloodPressureSYS").value.toString().toIntOrNull() ?: 0
                    val BMI = data.child("BMI").value.toString().toFloatOrNull() ?: 0f
                    val date = data.child("date").value.toString()
                    val task = data.child("recommendation").child("tasks").value as? List<Map<String, Any>> ?: emptyList()

                    val resultData = ResultData(journalID, bloodSugar, diastolicBP, systolicBP, BMI, date, task)
                    healthDataList.add(resultData)
                }

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                healthDataList.sortByDescending { it.date.let { date -> dateFormat.parse(date) } }

                mainAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error", error.message)
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateTodayReport(referencePath: String) {
        if (!::user.isInitialized || user.currentUser == null) {
            Log.e("MainActivity", "Pengguna belum login, tidak bisa ambil laporan hari ini.")
            return
        }

        val userID = user.currentUser!!.uid
        database.getReference("users").child(userID).child("journal").child(referencePath)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val bmiValue = it.result.child("BMI").value.toString().toDoubleOrNull() ?: 0.0
                    binding.tvBloodsugarLevel.text = it.result.child("bloodSugar").value.toString() + " mg/dL"
                    binding.tvBloodsugarDesc.text = it.result.child("recommendation").child("bloodSugarAnalysis").value.toString()
                    binding.tvBloodpressureLevel.text = it.result.child("bloodPressureSYS").value.toString() + "/" + it.result.child("bloodPressureDIA").value.toString() + " mm Hg"
                    binding.tvBloodpressureDesc.text = it.result.child("recommendation").child("bloodPressureAnalysis").value.toString()
                    binding.tvBmiLevel.text = String.format(Locale.getDefault(), "%.2f BMI", bmiValue)
                    binding.tvBmiDesc.text = it.result.child("recommendation").child("BMIAnalysis").value.toString()
                } else {
                    Log.e("MainActivity", "Gagal mengambil laporan hari ini: ${it.exception?.message}")
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}