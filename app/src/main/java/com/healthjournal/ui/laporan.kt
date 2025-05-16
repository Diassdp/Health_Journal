package com.healthjournal.ui

import MyMarkerView
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.healthjournal.R
import com.healthjournal.data.ResultData
import com.healthjournal.ui.journal.detail.DetailJournalActivity
import java.text.SimpleDateFormat
import java.util.*

class laporan : AppCompatActivity() {

    private lateinit var chartBMI: LineChart
    private lateinit var chartSugar: LineChart
    private lateinit var chartPressure: LineChart

    private lateinit var resultList: List<ResultData>
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private lateinit var sharedMarkerView: MyMarkerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        supportActionBar?.title = "Laporan Kesehatan"

        chartBMI = findViewById(R.id.chartBMI)
        chartSugar = findViewById(R.id.chartSugar)
        chartPressure = findViewById(R.id.chartPressure)

        @Suppress("UNCHECKED_CAST")
        resultList = intent.getSerializableExtra("healthDataList") as? List<ResultData> ?: emptyList()
        renderCharts(resultList)
    }

    private fun renderCharts(data: List<ResultData>) {
        val labels = ArrayList<String>()
        val bmiEntries = ArrayList<Entry>()
        val sugarEntries = ArrayList<Entry>()
        val systolicEntries = ArrayList<Entry>()
        val diastolicEntries = ArrayList<Entry>()

        val sorted = data.sortedBy { sdf.parse(it.date) }

        sorted.forEachIndexed { index, item ->
            labels.add("[${getIndonesianDayName(item.date)}] \n${item.date}")
            bmiEntries.add(Entry(index.toFloat(), item.BMI))
            sugarEntries.add(Entry(index.toFloat(), item.bloodSugar))
            systolicEntries.add(Entry(index.toFloat(), item.systolicBP.toFloat()))
            diastolicEntries.add(Entry(index.toFloat(), item.diastolicBP.toFloat()))
        }

        sharedMarkerView = MyMarkerView(this, sorted)

        drawChart(chartBMI, listOf(LineDataSet(bmiEntries, "BMI").apply {
            val color = ContextCompat.getColor(this@laporan, R.color.chart_bmi)
            setColor(color)
            setCircleColor(color)
            lineWidth = 2f
            valueTextSize = 10f
        }), labels)

        drawChart(chartSugar, listOf(LineDataSet(sugarEntries, "Gula Darah").apply {
            val color = ContextCompat.getColor(this@laporan, R.color.chart_sugar)
            setColor(color)
            setCircleColor(color)
            lineWidth = 2f
            valueTextSize = 10f
        }), labels)

        drawChart(chartPressure, listOf(
            LineDataSet(systolicEntries, "Sistolik").apply {
                val color = ContextCompat.getColor(this@laporan, R.color.chart_systolic)
                setColor(color)
                setCircleColor(color)
                lineWidth = 2f
                valueTextSize = 10f
            },
            LineDataSet(diastolicEntries, "Diastolik").apply {
                val color = ContextCompat.getColor(this@laporan, R.color.chart_diastolic)
                setColor(color)
                setCircleColor(color)
                lineWidth = 2f
                valueTextSize = 10f
            }
        ), labels)

        setupHighlightListeners()
        setupDoubleTapNavigation(chartBMI, sorted)
        setupDoubleTapNavigation(chartSugar, sorted)
        setupDoubleTapNavigation(chartPressure, sorted)
    }

    private fun drawChart(chart: LineChart, dataSets: List<ILineDataSet>, labels: List<String>) {
        chart.data = LineData(dataSets)

        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelRotationAngle = -45f
        }

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = true

        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        chart.setDoubleTapToZoomEnabled(false)
        chart.setVisibleXRangeMaximum(7f)
        chart.moveViewToX(chart.data.entryCount.toFloat())

        chart.marker = sharedMarkerView
        chart.invalidate()
    }

    private fun setupHighlightListeners() {
        val charts = listOf(chartBMI, chartSugar, chartPressure)

        for (chart in charts) {
            chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    charts.filter { it != chart }.forEach {
                        it.highlightValue(null, true)
                    }
                }

                override fun onNothingSelected() {}
            })
        }
    }

    private fun setupDoubleTapNavigation(chart: LineChart, data: List<ResultData>) {
        var lastTapTime = 0L
        var lastEntry: Entry? = null

        chart.setOnChartGestureListener(object : OnChartGestureListener {
            override fun onChartSingleTapped(me: MotionEvent?) {
                me ?: return
                val highlight = chart.getHighlightByTouchPoint(me.x, me.y)
                val entry = highlight?.let { chart.data.getEntryForHighlight(it) }

                val currentTime = System.currentTimeMillis()
                if (entry != null && lastEntry == entry && currentTime - lastTapTime < 400) {
                    val index = entry.x.toInt()
                    if (index in data.indices) {
                        val item = data[index]
                        val intent = Intent(this@laporan, DetailJournalActivity::class.java).apply {
                            putExtra("JOURNAL_KEY", item.journalID)
                        }
                        startActivity(intent)
                    }
                }

                lastEntry = entry
                lastTapTime = currentTime
            }

            override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
            override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
            override fun onChartLongPressed(me: MotionEvent?) {}
            override fun onChartDoubleTapped(me: MotionEvent?) {}
            override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {}
            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
        })
    }

    private fun getIndonesianDayName(dateString: String): String {
        return try {
            val date = sdf.parse(dateString) ?: return ""
            val locale = Locale("id", "ID")
            val dayFormat = SimpleDateFormat("EEEE", locale)
            dayFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

}
