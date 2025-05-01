package com.example.seguranca_trabalho

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.TextView
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

class CadastroActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tela_cadastro)

        // Inicializa o Firebase Analytics
        analytics = FirebaseAnalytics.getInstance(this)

        // Envia um evento de teste ao abrir a tela
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "app_start")
        }
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

        // Log para verificar se o evento foi disparado
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
    }
}
