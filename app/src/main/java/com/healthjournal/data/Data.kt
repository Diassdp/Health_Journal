package com.healthjournal.data

import com.google.android.gms.tasks.Task
import java.io.Serializable


data class HealthData(
    val bloodSugar: Float,
    val diastolicBP: Int,
    val systolicBP: Int,
    val BMI: Float,
    val age: Int,
    val gender: String
    )

data class ResultData(
    val journalID: String,
    val bloodSugar: Float,
    val diastolicBP: Int,
    val systolicBP: Int,
    val BMI: Float,
    val date: String,
    val task: List<Map<String, Any>>
    ) : Serializable

