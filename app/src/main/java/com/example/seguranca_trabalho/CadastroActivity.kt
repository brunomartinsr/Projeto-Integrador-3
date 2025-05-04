package com.example.seguranca_trabalho

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.util.Log
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONObject

class CadastroActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tela_cadastro)

        analytics = FirebaseAnalytics.getInstance(this)

        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "app_start")
        }
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        Log.d("FIREBASE_EVENT", "Evento de login enviado ao FirebaseAnalytics.")

        val textTenhoAcesso = findViewById<TextView>(R.id.textTenhoAcesso)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textTenhoAcesso.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val editNome = findViewById<EditText>(R.id.editNome)
        val editSetor = findViewById<EditText>(R.id.editSetor)
        val editCargo = findViewById<EditText>(R.id.editCargo)
        val editRf = findViewById<EditText>(R.id.editRf)

        btnEntrar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val setor = editSetor.text.toString().trim()
            val cargo = editCargo.text.toString().trim()
            val rf = editRf.text.toString().trim()

            if (nome.isNotEmpty() && setor.isNotEmpty() && cargo.isNotEmpty() && rf.isNotEmpty()) {
                Log.e("VALIDACAO", "enviando cadastro.")
                enviarCadastro(nome, setor, cargo, rf)
            } else {
                Log.e("VALIDACAO", "Preencha todos os campos.")
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun enviarCadastro(nome: String, setor: String, cargo: String, rf: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:3000/cadastro"

        val jsonBody = JSONObject().apply {
            put("nome", nome)
            put("setor", setor)
            put("cargo", cargo)
            put("rf", rf)
        }

        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                Log.d("API_RESPONSE", "Cadastro realizado: $response")
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_LONG).show()
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode
                if (statusCode == 409) {
                    Log.e("API_ERROR", "RF já cadastrado")
                    Toast.makeText(this, "RF já está cadastrado", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("API_ERROR", "Erro ao cadastrar: ${error.message}")
                    Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_LONG).show()
                }
            }
        ) {
            override fun getBodyContentType(): String = "application/json; charset=utf-8"
            override fun getBody(): ByteArray = jsonBody.toString().toByteArray(Charsets.UTF_8)
        }

        queue.add(request)
    }
}
