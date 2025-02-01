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
        when {
            healthData.bloodSugar > 140 -> {
                tasks.add(mapOf("task" to "Kurangi konsumsi makanan tinggi gula dan karbohidrat olahan.", "completed" to false))
                tasks.add(mapOf("task" to "Perbanyak konsumsi makanan berserat seperti sayuran dan biji-bijian.", "completed" to false))
                tasks.add(mapOf("task" to "Lakukan olahraga ringan setelah makan untuk membantu mengontrol gula darah.", "completed" to false))
            }
            healthData.bloodSugar < 70 -> {
                tasks.add(mapOf("task" to "Sediakan camilan sehat seperti buah atau kacang.", "completed" to false))
                tasks.add(mapOf("task" to "Jangan melewatkan makan, usahakan makan dalam porsi kecil tapi sering.", "completed" to false))
                tasks.add(mapOf("task" to "Konsumsi sumber karbohidrat kompleks untuk menjaga kadar gula tetap stabil.", "completed" to false))
            }
            else -> {
                tasks.add(mapOf("task" to "Pertahankan pola makan sehat dan seimbang untuk menjaga kadar gula darah stabil.", "completed" to false))
            }
        }

        // Task for Blood Pressure Control
        when {
            healthData.systolicBP > 120 || healthData.diastolicBP > 80 -> {
                tasks.add(mapOf("task" to "Lakukan olahraga ringan seperti jalan kaki selama 30 menit.", "completed" to false))
                tasks.add(mapOf("task" to "Kurangi konsumsi garam dan makanan berlemak.", "completed" to false))
                tasks.add(mapOf("task" to "Konsumsi lebih banyak makanan tinggi kalium seperti pisang dan bayam.", "completed" to false))
            }
            healthData.systolicBP < 90 -> {
                tasks.add(mapOf("task" to "Perbanyak minum air putih dan istirahat cukup.", "completed" to false))
                tasks.add(mapOf("task" to "Konsumsi makanan bergizi yang bisa membantu menaikkan tekanan darah.", "completed" to false))
            }
            else -> {
                tasks.add(mapOf("task" to "Jaga pola hidup sehat agar tekanan darah tetap stabil.", "completed" to false))
            }
        }

        // Task for BMI Control
        when {
            healthData.BMI > 25 -> {
                tasks.add(mapOf("task" to "Lakukan olahraga rutin minimal 3 kali seminggu.", "completed" to false))
                tasks.add(mapOf("task" to "Konsumsi lebih banyak sayuran dan serat.", "completed" to false))
                tasks.add(mapOf("task" to "Kurangi makanan tinggi lemak dan gula.", "completed" to false))
            }
            healthData.BMI < 18.5 -> {
                tasks.add(mapOf("task" to "Tambahkan makanan tinggi kalori seperti kacang dan susu.", "completed" to false))
                tasks.add(mapOf("task" to "Pastikan asupan protein dan karbohidrat mencukupi.", "completed" to false))
            }
            else -> {
                tasks.add(mapOf("task" to "Pertahankan berat badan ideal dengan pola makan dan olahraga seimbang.", "completed" to false))
            }
        }

        // Age and Gender Specific Tasks
        if (healthData.age > 50) {
            tasks.add(mapOf("task" to "Lakukan pemeriksaan kesehatan rutin setiap bulan.", "completed" to false))
            tasks.add(mapOf("task" to "Jaga kesehatan tulang dengan konsumsi kalsium dan vitamin D.", "completed" to false))
        }

        when (healthData.gender) {
            "Female", "Other" -> {
                tasks.add(mapOf("task" to "Perhatikan kebutuhan kalsium dan zat besi, terutama saat menstruasi atau menopause.", "completed" to false))
                tasks.add(mapOf("task" to "Pastikan mendapatkan asupan vitamin D yang cukup.", "completed" to false))
            }
            "Male" -> {
                tasks.add(mapOf("task" to "Perhatikan kesehatan prostat dengan pola makan sehat.", "completed" to false))
                tasks.add(mapOf("task" to "Jaga kesehatan jantung dengan olahraga teratur.", "completed" to false))
            }
        }

        return tasks
    }
}
