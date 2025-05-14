package com.healthjournal.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.healthjournal.R

class laporan : AppCompatActivity() {

    private lateinit var lineChart: LineChart

    private val jsonString = """
        {
            "journal": {
                "-OI9BcS6G4-Ftau4WJe0": {
                    "BMI": 22.7731876373291,
                    "bloodPressureDIA": "76",
                    "bloodPressureSYS": "113",
                    "bloodSugar": "80",
                    "date": "03/02/2025"
                },
                "-OIJnaHgm2dP_Mbzu2Ta": {
                    "BMI": 22.7731876373291,
                    "bloodPressureDIA": "72",
                    "bloodPressureSYS": "109",
                    "bloodSugar": "89",
                    "date": "05/02/2025"
                }
            }
        }
    """

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart = findViewById(R.id.lineChart)
        renderChart()
    }

    private fun renderChart() {
        val labels = ArrayList<String>()
        val bmiEntries = ArrayList<Entry>()
        val sugarEntries = ArrayList<Entry>()
        val pressureEntries = ArrayList<Entry>() // We'll use SYS for simplicity

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val chartData = JSONObject(jsonString).getJSONObject("journal")
        val keys = chartData.keys().asSequence().sortedBy {
            sdf.parse(chartData.getJSONObject(it).getString("date"))
        }.toList()

        keys.forEachIndexed { index, key ->
            val entry = chartData.getJSONObject(key)
            val date = entry.getString("date")
            val bmi = entry.getDouble("BMI").toFloat()
            val sugar = entry.getString("bloodSugar").toFloat()
            val pressure = entry.getString("bloodPressureSYS").toFloat()

            labels.add(date)
            bmiEntries.add(Entry(index.toFloat(), bmi))
            sugarEntries.add(Entry(index.toFloat(), sugar))
            pressureEntries.add(Entry(index.toFloat(), pressure))
        }

        val bmiDataSet = LineDataSet(bmiEntries, "BMI").apply {
            color = resources.getColor(android.R.color.holo_green_dark)
            setCircleColor(color)
        }

        val sugarDataSet = LineDataSet(sugarEntries, "Blood Sugar").apply {
            color = resources.getColor(android.R.color.holo_red_dark)
            setCircleColor(color)
        }

        val pressureDataSet = LineDataSet(pressureEntries, "Blood Pressure (SYS)").apply {
            color = resources.getColor(android.R.color.holo_blue_dark)
            setCircleColor(color)
        }

        val lineData = LineData(bmiDataSet, sugarDataSet, pressureDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        lineChart.axisRight.isEnabled = false
        lineChart.description.text = "Health Metrics Over Time"
        lineChart.invalidate()
    }
}