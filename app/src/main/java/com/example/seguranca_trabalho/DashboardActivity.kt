package com.example.seguranca_trabalho

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        val pieChart = findViewById<PieChart>(R.id.pieChart)

        // Garantir que o gráfico está visível
        pieChart.setNoDataText("Carregando dados...")

        // Dados para o gráfico
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(60f, "Concluídas"))
        entries.add(PieEntry(25f, "Abertas"))
        entries.add(PieEntry(15f, "Em Andamento"))

        // Verifica se os dados foram criados
        if (entries.isEmpty()) {
            pieChart.setNoDataText("Sem dados")
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 16f

        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK)
        pieChart.centerText = "Ocorrências"
        pieChart.setCenterTextSize(18f)
        pieChart.animateY(1200)

        // IMPORTANTE: Atualiza o gráfico
        pieChart.invalidate()
    }
}
