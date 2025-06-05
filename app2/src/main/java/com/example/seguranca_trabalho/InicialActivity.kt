package com.example.seguranca_trabalho

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.seguranca_trabalho.R
import com.example.seguranca_trabalho.Registro
import com.example.seguranca_trabalho.RegistroAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray

class InicialActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var recyclerView: RecyclerView
    private val listaRegistros = mutableListOf<Registro>()
    private lateinit var googleMap: GoogleMap
    private lateinit var txtPoucoGrave: TextView
    private lateinit var txtGrave: TextView
    private lateinit var txtMuitoGrave: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_inicial)

        txtPoucoGrave = findViewById(R.id.txtPoucoGrave)
        txtGrave = findViewById(R.id.txtGrave)
        txtMuitoGrave = findViewById(R.id.txtMuitoGrave)
        recyclerView = findViewById(R.id.recyclerViewRegistros)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RegistroAdapter(listaRegistros)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        carregarRegistros()
    }

    private fun carregarRegistros() {
        val url = "http://192.168.0.126:3000/tela_registros"// 10.0.2.2 para localhost no emulador

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
                listaRegistros.clear()

                var poucoGrave = 0
                var grave = 0
                var muitoGrave = 0

                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val registro = Registro(
                        id = obj.optString("_id"),
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

                    when (registro.gravidade.trim().lowercase()) {
                        "pouco grave" -> poucoGrave++
                        "grave" -> grave++
                        "muito grave" -> muitoGrave++
                    }

                    adicionarMarcadorNoMapa(registro.geo, registro.descricao)
                }

                txtPoucoGrave.text = "Pouco grave: $poucoGrave"
                txtGrave.text = "Grave: $grave"
                txtMuitoGrave.text = "Muito grave: $muitoGrave"

                recyclerView.adapter?.notifyDataSetChanged()
            },
            { error ->
                Log.e("ErroVolley", "erro ao buscar registros: ${error.message}")
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun adicionarMarcadorNoMapa(geo: String, descricao: String) {
        val partes = geo.split(",")
        if (partes.size == 2) {
            try {
                val lat = partes[0].toDouble()
                val lng = partes[1].toDouble()
                val posicao = LatLng(lat, lng)
                googleMap.addMarker(MarkerOptions().position(posicao).title(descricao))
            } catch (e: Exception) {
                Log.e("Mapa", "Erro ao converter coordenadas: $geo")
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val posicaoInicial = LatLng(-22.3877, -46.9234)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoInicial, 16f))
    }
}
