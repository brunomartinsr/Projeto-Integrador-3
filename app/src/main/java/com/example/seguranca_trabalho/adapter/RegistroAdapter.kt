import com.example.seguranca_trabalho.model.Registro
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seguranca_trabalho.R

class RegistroAdapter(private val registros: List<Registro>) :
    RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Descricao: TextView = itemView.findViewById(R.id.txtDescricao)
        val Gravidade: TextView = itemView.findViewById(R.id.txtGravidade)
        val Local: TextView = itemView.findViewById(R.id.txtLocal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        holder.Descricao.text = "Descrição: ${registro.descricao}"
        holder.Gravidade.text = "Gravidade: ${registro.gravidade}"
        holder.Local.text = "Local: ${registro.local}"
    }

    override fun getItemCount() = registros.size
}
