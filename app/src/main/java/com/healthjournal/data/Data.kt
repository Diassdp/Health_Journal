package com.healthjournal.data

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
    val date: String
    ) : Serializable

