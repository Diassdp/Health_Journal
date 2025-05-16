import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.healthjournal.R
import com.healthjournal.data.ResultData
import com.healthjournal.ui.journal.detail.DetailJournalActivity

class MyMarkerView(
    private val context: Context,
    private val dataList: List<ResultData>
) : MarkerView(context, R.layout.marker_view) {

    private val tvContent: TextView = findViewById(R.id.tvContent)
    private var lastHighlightedIndex: Int? = null

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val index = e.x.toInt()
            lastHighlightedIndex = index
            if (index in dataList.indices) {
                val item = dataList[index]
                val content = """
                    Hari: ${getIndonesianDayName(item.date)}
                    Tanggal: ${item.date}
                    Gula Darah: ${item.bloodSugar}
                    BMI: ${item.BMI.toString().substring(0, 4)}
                    Tekanan Darah: ${item.systolicBP}/${item.diastolicBP} mmHg
                """.trimIndent()
                tvContent.text = content
            } else {
                tvContent.text = ""
            }
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }

    private fun getIndonesianDayName(dateString: String): String {
        return try {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("id", "ID"))
            val date = sdf.parse(dateString)
            if (date != null) {
                val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale("id", "ID"))
                dayFormat.format(date)
            } else {
                "-"
            }
        } catch (e: Exception) {
            "-"
        }
    }
}
