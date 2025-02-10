package com.healthjournal.ui.dashboard

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.healthjournal.R
import com.healthjournal.data.ResultData
import com.healthjournal.ui.journal.detail.DetailJournalActivity
import java.text.SimpleDateFormat
import java.util.Locale

class MainAdapter(private val healthDataList: MutableList<ResultData>) : RecyclerView.Adapter<MainAdapter.HealthViewHolder>() {

    class HealthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tv_day)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvBloodSugar: TextView = view.findViewById(R.id.tv_BS_Level)
        val tvBloodPressure: TextView = view.findViewById(R.id.tv_BP_Level)
        val tvGoals: TextView = view.findViewById(R.id.tv_goals_count)
    }

    // Function to update the list and notify the adapter
    fun setData(newData: List<ResultData>) {
        healthDataList.clear()
        healthDataList.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_history, parent, false)
        return HealthViewHolder(view)
    }

    private fun dayOfWeek(date: String): String {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = dateFormat.parse(date)
            if (parsedDate != null) {
                SimpleDateFormat("EEEE", Locale.getDefault()).format(parsedDate)
            } else {
                "Invalid Date"
            }
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    private fun countGoals(taskList: List<Map<String, Any>>): String {
        Log.d("debug", taskList.toString())
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

    override fun onBindViewHolder(holder: HealthViewHolder, position: Int) {
        val healthData = healthDataList[position]

        holder.tvDay.text = dayOfWeek(healthData.date)
        holder.tvDate.text = healthData.date
        holder.tvBloodSugar.text = "${healthData.bloodSugar} mg/dL"
        holder.tvBloodPressure.text = "${healthData.diastolicBP}/${healthData.systolicBP} mm Hg"
        holder.tvGoals.text = countGoals(healthData.task)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailJournalActivity::class.java).apply {
                putExtra("JOURNAL_KEY", healthData.journalID)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = healthDataList.size
}
