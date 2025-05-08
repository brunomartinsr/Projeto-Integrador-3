package com.example.seguranca_trabalho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts


class RegistrarActivity : AppCompatActivity() {

    private lateinit var inputDescricao: EditText
    private lateinit var inputLocal: EditText
    private lateinit var radioGroupGravidade: RadioGroup
    private lateinit var btnRegistrar: Button
    private lateinit var btnVoltar: ImageView
    private lateinit var btnSelecionarFoto: Button
    private lateinit var imagePreview: ImageView
    private var imagemSelecionadaUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_registrar)

        inputDescricao = findViewById(R.id.inputDescricao)
        inputLocal = findViewById(R.id.inputLocal)
        radioGroupGravidade = findViewById(R.id.radioGroupGravidade)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnVoltar = findViewById(R.id.btnVoltar)
        btnSelecionarFoto = findViewById(R.id.btnSelecionarFoto)
        imagePreview = findViewById(R.id.imagePreview)

        btnVoltar.setOnClickListener {
            finish()
        }

        btnRegistrar.setOnClickListener {
            registrarRisco()
        }

        btnSelecionarFoto.setOnClickListener {
            selecionarImagemGaleria()
        }
    }

    private val selecionarImagem =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imagemSelecionadaUri = data?.data
                imagePreview.setImageURI(imagemSelecionadaUri)
            }
        }

    private fun selecionarImagemGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        selecionarImagem.launch(intent)
    }


    private fun registrarRisco() {
        val descricao = inputDescricao.text.toString().trim()
        val local = inputLocal.text.toString().trim()
        val gravidadeId = radioGroupGravidade.checkedRadioButtonId

        if (descricao.isEmpty() || local.isEmpty() || gravidadeId == -1) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val radioButtonSelecionado = findViewById<RadioButton>(gravidadeId)
        val gravidade = radioButtonSelecionado.text.toString()

        // Aqui você pode enviar os dados para o banco de dados ou backend
        Log.d("RegistrarActivity", "Descrição: $descricao")
        Log.d("RegistrarActivity", "Gravidade: $gravidade")
        Log.d("RegistrarActivity", "Local: $local")

        Toast.makeText(this, "Risco registrado com sucesso!", Toast.LENGTH_SHORT).show()

        // Limpa os campos se quiser
        inputDescricao.text.clear()
        inputLocal.text.clear()
        radioGroupGravidade.clearCheck()
    }
}
