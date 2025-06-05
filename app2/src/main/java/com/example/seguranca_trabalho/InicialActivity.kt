package com.example.seguranca_trabalho

import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_inicial)

        recyclerView = findViewById(R.id.recyclerViewRegistros)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RegistroAdapter(listaRegistros)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        carregarRegistros()
    }

    private fun carregarRegistros() {
        val url = "http://192.168.0.126:3000/tela_registros" // 10.0.2.2 para localhost no emulador Android

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
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

                    adicionarMarcadorNoMapa(registro.geo, registro.descricao)
                }
                recyclerView.adapter?.notifyDataSetChanged()
            },
            { error ->
                Log.e("ErroVolley", "Erro ao buscar registros: ${error.message}")
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
