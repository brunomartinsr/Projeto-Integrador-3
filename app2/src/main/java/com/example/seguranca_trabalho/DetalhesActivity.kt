package com.example.seguranca_trabalho

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DetalhesActivity : AppCompatActivity() {

    private lateinit var etDescricao: EditText
    private lateinit var etStatus: EditText
    private lateinit var etGravidade: EditText
    private lateinit var etLocal: EditText
    private lateinit var btnSalvar: Button
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_detalhes)

        Toast.makeText(this, "DetalhesActivity aberta", Toast.LENGTH_SHORT).show()
        Log.d("DetalhesActivity", "onCreate foi chamado")

        etDescricao = findViewById(R.id.etDescricao)
        etStatus = findViewById(R.id.etStatus)
        etGravidade = findViewById(R.id.etGravidade)
        etLocal = findViewById(R.id.etLocal)
        btnSalvar = findViewById(R.id.btnSalvar)

        id = intent.getStringExtra("id") ?: ""
        Log.d("DetalhesActivity", "ID recebido: $id")
        etDescricao.setText(intent.getStringExtra("descricao"))
        etStatus.setText(intent.getStringExtra("status"))
        etGravidade.setText(intent.getStringExtra("gravidade"))
        etLocal.setText(intent.getStringExtra("local"))

        btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun salvarAlteracoes() {
        val url = "http://192.168.0.126:3000/editar_registro/$id"

        val json = JSONObject().apply {
            put("descricao", etDescricao.text.toString())
            put("status", etStatus.text.toString())
            put("gravidade", etGravidade.text.toString())
            put("local", etLocal.text.toString())
        }

        val request = JsonObjectRequest(Request.Method.PUT, url, json,
            {
                Toast.makeText(this, "Registro atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            },
            {
                Toast.makeText(this, "Erro ao atualizar registro", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
