package com.example.seguranca_trabalho

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class InicialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val listaRegistros = mutableListOf<Registro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_inicial)

        recyclerView = findViewById(R.id.recyclerViewRegistros)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RegistroAdapter(listaRegistros)

        carregarRegistros()
    }

    private fun carregarRegistros() {
        val url = "http://192.168.0.126:3000/tela_registros" // Use 10.0.2.2 para localhost no emulador Android

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val registro = Registro(
                        id = obj.optString("id"),
                        rf = obj.optString("rf"),
                        descricao = obj.optString("descricao"),
                        status = obj.optString("status"),
                        gravidade = obj.optString("gravidade"),
                        local = obj.optString("local"),
                        geo = obj.optString("geo"),
                        fotoUrl = obj.optString("fotoUrl"),
                        data = obj.optString("data")
                    )
                    listaRegistros.add(registro)
                }
                recyclerView.adapter?.notifyDataSetChanged()
            },
            { error ->
                Log.e("ErroVolley", "Erro ao buscar registros: ${error.message}")
            })

        Volley.newRequestQueue(this).add(request)
    }
}



