package com.example.seguranca_trabalho

import RegistroAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import android.content.Intent
import com.example.seguranca_trabalho.R
import com.example.seguranca_trabalho.RegistrarActivity
import com.example.seguranca_trabalho.model.Registro


class RegistrosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_registros)

        recyclerView = findViewById(R.id.recyclerViewRegistros)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnRegistrar = findViewById<Button>(R.id.btnEntrar)
        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistrarActivity::class.java))
        }

        buscarRegistros()
    }

    private fun buscarRegistros() {
        val request = Request.Builder()
            .url("http://192.168.0.126:3000/tela_registros")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERRO", "Erro na requisição: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val tipoLista = object : TypeToken<List<Registro>>() {}.type
                    val registros: List<Registro> = Gson().fromJson(json, tipoLista)

                    runOnUiThread {
                        recyclerView.adapter = RegistroAdapter(registros)
                    }
                }
            }
        })
    }
}
