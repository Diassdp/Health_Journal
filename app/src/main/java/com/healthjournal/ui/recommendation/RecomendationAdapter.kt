package com.healthjournal.ui.recommendation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.healthjournal.R

class RecommendationAdapter(
    private val recommendationList: List<Pair<String, Boolean>>,
    private val onCheckedChange: (position: Int, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {

    inner class RecommendationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGoalName: TextView = view.findViewById(R.id.tv_goal_name)
        val checkBox: CheckBox = view.findViewById(R.id.toggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goals_recommendation, parent, false)
        return RecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val (task, isCompleted) = recommendationList[position]
        holder.tvGoalName.text = task
        holder.checkBox.isChecked = isCompleted
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(position, isChecked)
        }
        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }

    override fun getItemCount(): Int = recommendationList.size
}
