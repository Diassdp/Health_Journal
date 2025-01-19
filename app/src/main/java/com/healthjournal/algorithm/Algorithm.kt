package com.healthjournal.algorithm

import com.healthjournal.data.HealthData

class AlgoritmaKesehatan {
    fun recommendationOfTheDay(healthData: HealthData): HashMap<String, Any> {
        val bloodSugarAnalysis = getBloodSugarAnalysis(healthData)
        val bloodPressureAnalysis = getBloodPressureAnalysis(healthData)
        val BMIAnalysis = getBMIAnalysis(healthData)
        val tasks = getTaskBasedData(healthData)

        return hashMapOf(
            "bloodSugarAnalysis" to bloodSugarAnalysis,
            "bloodPressureAnalysis" to bloodPressureAnalysis,
            "BMIAnalysis" to BMIAnalysis,
            "tasks" to tasks
        )
    }

    fun getBloodSugarAnalysis(healthData: HealthData): String {
        return when (healthData.gender) {
            "Female", "Other" -> when {
                healthData.bloodSugar < 70 -> "Gula darah rendah, konsumsi makanan manis seperti buah atau madu."
                healthData.bloodSugar in 70.0..140.0 -> "Gula darah normal, pertahankan pola makan sehat."
                healthData.bloodSugar > 140 -> "Gula darah tinggi, kurangi makanan manis dan lakukan olahraga teratur."
                else -> "Data gula darah tidak valid."
            }
            "Male" -> when {
                healthData.bloodSugar < 70 -> "Gula darah rendah, segera konsumsi makanan berkarbohidrat."
                healthData.bloodSugar in 70.0..130.0 -> "Gula darah normal, jaga pola makan seimbang."
                healthData.bloodSugar > 130 -> "Gula darah tinggi, batasi konsumsi gula dan periksa kesehatan secara berkala."
                else -> "Data gula darah tidak valid."
            }
            else -> "Data gula darah tidak valid."
        }
    }

    fun getBloodPressureAnalysis(healthData: HealthData): String {
        return when (healthData.gender) {
            "Female", "Other" -> when {
                healthData.systolicBP < 90 || healthData.diastolicBP < 60 -> "Tekanan darah rendah, tingkatkan asupan cairan dan garam."
                healthData.systolicBP in 90..120 && healthData.diastolicBP in 60..80 -> "Tekanan darah normal, pertahankan gaya hidup sehat."
                healthData.systolicBP > 120 || healthData.diastolicBP > 80 -> "Tekanan darah tinggi, kurangi garam dan tingkatkan aktivitas fisik."
                else -> "Data tekanan darah tidak valid."
            }
            "Male" -> when {
                healthData.systolicBP < 100 || healthData.diastolicBP < 60 -> "Tekanan darah rendah, perbanyak cairan dan istirahat."
                healthData.systolicBP in 100..130 && healthData.diastolicBP in 60..85 -> "Tekanan darah normal, pertahankan pola makan sehat."
                healthData.systolicBP > 130 || healthData.diastolicBP > 85 -> "Tekanan darah tinggi, konsultasikan dengan dokter untuk pemeriksaan lebih lanjut."
                else -> "Data tekanan darah tidak valid."
            }
            else -> "Data tekanan darah tidak valid."
        }
    }

    fun getBMIAnalysis(healthData: HealthData): String {
        return when (healthData.gender) {
            "Female", "Other" -> when {
                healthData.BMI < 18.5 -> "BMI rendah, tingkatkan asupan kalori dengan makanan bernutrisi."
                healthData.BMI in 18.5..24.9 -> "BMI normal, pertahankan pola makan seimbang."
                healthData.BMI > 24.9 -> "BMI tinggi, lakukan olahraga teratur dan perbaiki pola makan."
                else -> "Data BMI tidak valid."
            }
            "Male" -> when {
                healthData.BMI < 20 -> "BMI rendah, tambahkan asupan protein dan makanan bergizi."
                healthData.BMI in 20.0..25.0 -> "BMI normal, teruskan pola hidup sehat."
                healthData.BMI > 25 -> "BMI tinggi, perbanyak aktivitas fisik dan atur pola makan rendah lemak."
                else -> "Data BMI tidak valid."
            }
            else -> "Data BMI tidak valid."
        }
    }

    fun getTaskBasedData(healthData: HealthData): List<Map<String, Any>> {
        val tasks = mutableListOf<Map<String, Any>>()

        // Task for Blood Sugar Control
        if (healthData.bloodSugar > 140) {
            tasks.add(mapOf("task" to "Cek kadar gula darah 2 kali sehari.", "completed" to false))
            tasks.add(mapOf("task" to "Kurangi konsumsi makanan tinggi gula.", "completed" to false))
        } else if (healthData.bloodSugar < 70) {
            tasks.add(mapOf("task" to "Sediakan camilan sehat seperti buah atau kacang.", "completed" to false))
        }

        // Task for Blood Pressure Control
        if (healthData.systolicBP > 120 || healthData.diastolicBP > 80) {
            tasks.add(mapOf("task" to "Lakukan olahraga ringan seperti jalan kaki selama 30 menit.", "completed" to false))
            tasks.add(mapOf("task" to "Kurangi makanan asin dan berlemak.", "completed" to false))
        } else if (healthData.systolicBP < 90) {
            tasks.add(mapOf("task" to "Perbanyak minum air putih dan istirahat.", "completed" to false))
        }

        // Task for BMI Control
        if (healthData.BMI > 25) {
            tasks.add(mapOf("task" to "Lakukan olahraga rutin minimal 3 kali seminggu.", "completed" to false))
            tasks.add(mapOf("task" to "Konsumsi lebih banyak sayuran dan serat.", "completed" to false))
        } else if (healthData.BMI < 18.5) {
            tasks.add(mapOf("task" to "Tambahkan makanan tinggi kalori seperti kacang dan susu.", "completed" to false))
        }

        // Age and Gender Specific Tasks
        if (healthData.age > 50) {
            tasks.add(mapOf("task" to "Lakukan pemeriksaan kesehatan rutin setiap bulan.", "completed" to false))
        }
        if (healthData.gender == "Female" || healthData.gender == "Other") {
            tasks.add(mapOf("task" to "Perhatikan kebutuhan kalsium dan zat besi.", "completed" to false))
        }

        return tasks
    }
}
