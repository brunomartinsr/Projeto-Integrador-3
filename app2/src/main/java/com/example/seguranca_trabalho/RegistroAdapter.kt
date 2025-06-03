package com.example.seguranca_trabalho

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegistroAdapter(private val lista: List<Registro>) :
    RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descricao: TextView = itemView.findViewById(R.id.tvDescricao)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val gravidade: TextView = itemView.findViewById(R.id.tvGravidade)
        val local: TextView = itemView.findViewById(R.id.tvLocal)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val item = lista[position]
        holder.descricao.text = "Descrição: ${item.descricao}"
        holder.status.text = "Status: ${item.status}"
        holder.gravidade.text = "Gravidade: ${item.gravidade}"
        holder.local.text = "Local: ${item.local}"

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalhesActivity::class.java).apply {
                putExtra("id", item.id)
                putExtra("rf", item.rf)
                putExtra("descricao", item.descricao)
                putExtra("status", item.status)
                putExtra("gravidade", item.gravidade)
                putExtra("local", item.local)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lista.size
}
