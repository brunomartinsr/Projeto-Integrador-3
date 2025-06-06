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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import android.location.Location
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class RegistrarActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var inputDescricao: EditText
    private lateinit var inputLocal: EditText
    private lateinit var radioGroupGravidade: RadioGroup
    private lateinit var btnRegistrar: Button
    private lateinit var btnVoltar: ImageView
    private lateinit var btnSelecionarFoto: Button
    private lateinit var imagePreview: ImageView
    private var imagemSelecionadaUri: Uri? = null
    private var localizacaoAtual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_registrar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obterLocalizacaoAtual()

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

        btnSelecionarFoto.setOnClickListener {
            selecionarImagemGaleria()
        }

        btnRegistrar.setOnClickListener {
            val rfFuncionario = intent.getStringExtra("rf") ?: "desconhecido"
            if (imagemSelecionadaUri != null) {
                uploadImagemParaFirebase(imagemSelecionadaUri!!, rfFuncionario)
            } else {
                salvarNoApiBackend("", rfFuncionario)
            }
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

    private fun obterLocalizacaoAtual() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        val locationRequest = LocationRequest.Builder(10000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(5000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    location?.let { loc ->
                        localizacaoAtual = "${loc.latitude},${loc.longitude}"
                        Log.d("LOCALIZAÇÃO", "Lat/Lon: $localizacaoAtual")
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            mainLooper
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacaoAtual()
        } else {
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
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

        val json = """
            {
                "rf": "$rfFuncionario",
                "descricao": "$descricao",
                "status": "Aberto",
                "gravidade": "$gravidade",
                "local": "$local",
                "geo": "$localizacaoAtual",
                "fotoUrl": "$imagemUrl"
            }
        """.trimIndent()

        val requestBody = okhttp3.RequestBody.create(
            "application/json; charset=utf-8".toMediaType(), json
        )

        val request = okhttp3.Request.Builder()
            .url("http://192.168.0.126:3000/registrar")
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
