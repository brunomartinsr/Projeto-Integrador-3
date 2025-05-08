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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException

data class RegistroDePerigo(
    val rf: String = "",
    val descricao: String = "",
    val status: String = "Aberto",
    val gravidade: String = "",
    val local: String = "",
    val geo: String = "",
    val fotoUrl: String = ""
)

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
            val rfFuncionario = intent.getStringExtra("rf") ?: "desconhecido"

            if (imagemSelecionadaUri != null) {
                uploadImagemParaFirebase(imagemSelecionadaUri!!, rfFuncionario)
            } else {
                salvarNoApiBackend("", rfFuncionario)
            }
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

    private fun uploadImagemParaFirebase(uri: Uri, rfFuncionario: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val nomeArquivo = "imagens/${System.currentTimeMillis()}.jpg"
        val imagemRef = storageRef.child(nomeArquivo)

        val uploadTask = imagemRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            imagemRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                Log.d("URL_FIREBASE", "URL gerada: $downloadUrl")
                salvarNoApiBackend(downloadUrl.toString(), rfFuncionario)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Falha ao enviar imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarNoApiBackend(imagemUrl: String, rfFuncionario: String) {
        val descricao = inputDescricao.text.toString()
        val local = inputLocal.text.toString()
        val gravidadeSelecionadaId = radioGroupGravidade.checkedRadioButtonId
        val gravidade = if (gravidadeSelecionadaId != -1) {
            findViewById<RadioButton>(gravidadeSelecionadaId).text.toString()
        } else {
            "Não informada"
        }
        Log.d("REGISTRO", "RF: $rfFuncionario")
        Log.d("REGISTRO", "Descrição: $descricao")
        Log.d("REGISTRO", "Gravidade: $gravidade")
        Log.d("REGISTRO", "Local: $local")
        Log.d("REGISTRO", "Foto URL: $imagemUrl")

        val json = """
            {
                "rf": "$rfFuncionario",
                "descricao": "$descricao",
                "status": "Aberto",
                "gravidade": "$gravidade",
                "local": "$local",
                "geo": "latitude,longitude",
                "fotoUrl": "$imagemUrl"
            }
        """.trimIndent()

        val requestBody = okhttp3.RequestBody.create(
            "application/json; charset=utf-8".toMediaType(), json
        )

        val request = okhttp3.Request.Builder()
            .url("http://10.0.2.2:3000/registrar")
            .post(requestBody)
            .build()

        val client = okhttp3.OkHttpClient()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegistrarActivity, "Erro ao conectar com a API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        val registro = RegistroDePerigo(
                            rf = rfFuncionario,
                            descricao = descricao,
                            status = "Aberto",
                            gravidade = gravidade,
                            local = local,
                            geo = "latitude,longitude",
                            fotoUrl = imagemUrl
                        )

                        Toast.makeText(this@RegistrarActivity, "Registro enviado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegistrarActivity, "Erro no envio dos dados", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

}
