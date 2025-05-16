package com.healthjournal.ui.dashboard

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.healthjournal.R
import com.healthjournal.data.ResultData
import com.healthjournal.ui.journal.detail.DetailJournalActivity
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter(private val daftarDataKesehatan: MutableList<ResultData>) : RecyclerView.Adapter<MainAdapter.HolderDataKesehatan>() {

    class HolderDataKesehatan(view: View) : RecyclerView.ViewHolder(view) {
        val tvHari: TextView = view.findViewById(R.id.tv_day)
        val tvTanggal: TextView = view.findViewById(R.id.tv_date)
        val tvGulaDarah: TextView = view.findViewById(R.id.tv_BS_Level)
        val tvTekananDarah: TextView = view.findViewById(R.id.tv_BP_Level)
        val tvTugas: TextView = view.findViewById(R.id.tv_goals_count)
    }

    fun perbaruiData(dataBaru: List<ResultData>) {
        daftarDataKesehatan.clear()
        daftarDataKesehatan.addAll(dataBaru)
        notifyDataSetChanged()
    }

    private fun dapatkanHariDariTanggal(tanggal: String): String {
        return try {
            val formatTanggal = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
            val tanggalTerurai = formatTanggal.parse(tanggal)
            if (tanggalTerurai != null) {
                SimpleDateFormat("EEEE", Locale("id", "ID")).format(tanggalTerurai)
                    .replaceFirstChar { it.uppercaseChar() }
            } else {
                "Tanggal Tidak Valid"
            }
        } catch (e: Exception) {
            "Tanggal Tidak Valid"
        }
    }

    private fun hitungTugasRekomendasi(daftarTugas: List<Map<String, Any>>): String {
        val total = daftarTugas.size
        val selesai = daftarTugas.count { (it["completed"] as? Boolean) == true }

        return when {
            total == 0 -> "Tidak ada tugas"
            selesai == 0 -> "Belum ada tugas selesai"
            else -> "$selesai dari $total tugas selesai"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataKesehatan {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_history, parent, false)
        return HolderDataKesehatan(view)
    }

    override fun onBindViewHolder(holder: HolderDataKesehatan, posisi: Int) {
        val data = daftarDataKesehatan[posisi]

        holder.tvHari.text = dapatkanHariDariTanggal(data.date)
        holder.tvTanggal.text = data.date
        holder.tvGulaDarah.text = "${data.bloodSugar} mg/dL"
        holder.tvTekananDarah.text = "${data.systolicBP}/${data.diastolicBP} mmHg"
        holder.tvTugas.text = hitungTugasRekomendasi(data.task)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailJournalActivity::class.java).apply {
                putExtra("JOURNAL_KEY", data.journalID)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = daftarDataKesehatan.size
}
