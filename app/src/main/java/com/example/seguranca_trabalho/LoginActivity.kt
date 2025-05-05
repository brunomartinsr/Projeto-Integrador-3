package com.example.seguranca_trabalho

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_login)

        val editRF = findViewById<EditText>(R.id.editRF)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val textNaoTenhoAcesso = findViewById<TextView>(R.id.textNaoTenhoAcesso)

        btnEntrar.setOnClickListener {
            val rfDigitado = editRF.text.toString()

            if (rfDigitado.isNotEmpty()) {
                verificarRF(rfDigitado)
            } else {
                Toast.makeText(this, "Por favor, digite seu RF", Toast.LENGTH_SHORT).show()
            }
        }

        textNaoTenhoAcesso.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun verificarRF(rf: String) {
        // Mostra um toast de "Verificando..."
        Toast.makeText(this, "Verificando RF...", Toast.LENGTH_SHORT).show()

        // Usa coroutines para fazer operação de rede em background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Substitua pelo endereço real da sua API
                val url = URL("http://10.0.2.2:3000/login")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Prepara os dados para enviar
                val jsonInputString = JSONObject().apply {
                    put("rf", rf)
                }.toString()

                // Envia os dados
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonInputString)
                    writer.flush()
                }

                // Verifica a resposta
                val responseCode = connection.responseCode
                
                withContext(Dispatchers.Main) {
                    when (responseCode) {
                        200 -> {
                            // RF válido, salva localmente
                            val sharedPrefs = getSharedPreferences("dados_usuario", Context.MODE_PRIVATE)
                            val editor = sharedPrefs.edit()
                            editor.putString("rf", rf)
                            editor.apply()

                            Toast.makeText(this@LoginActivity, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                            
                            // Navegue para a próxima tela aqui
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Encerra a atividade de login
                        }
                        401 -> {
                            Toast.makeText(this@LoginActivity, "RF não encontrado", Toast.LENGTH_LONG).show()
                        }
                        400 -> {
                            Toast.makeText(this@LoginActivity, "RF não informado", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Erro ao fazer login: $responseCode", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                
                connection.disconnect()
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}