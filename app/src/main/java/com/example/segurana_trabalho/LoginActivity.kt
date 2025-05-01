package com.example.segurana_trabalho

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_login)

        val editRF = findViewById<EditText>(R.id.editRF)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val rfDigitado = editRF.text.toString()

            if (rfDigitado.isNotEmpty()) {
                val sharedPrefs = getSharedPreferences("dados_usuario", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                editor.putString("rf", rfDigitado)
                editor.apply()

                Toast.makeText(this, "RF registrado!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "RF inválido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

//Código para recuperar o RF:
//val sharedPrefs = getSharedPreferences("dados_usuario", Context.MODE_PRIVATE)
//val rfSalvo = sharedPrefs.getString("rf", "não encontrado")