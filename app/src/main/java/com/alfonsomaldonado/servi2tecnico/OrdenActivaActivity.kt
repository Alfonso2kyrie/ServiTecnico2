package com.alfonsomaldonado.servi2tecnico

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.telecom.TelecomManager.EXTRA_LOCATION
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_orden_activa.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.location.LocationManager
import androidx.annotation.NonNull
import com.alfonsomaldonado.servi2tecnico.chat.ChatActivity
import com.alfonsomaldonado.servi2tecnico.chat.TokenClass
import com.example.servi2.coneccion.ConexionBD


class OrdenActivaActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var idorden2: String
    private lateinit var token: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden_activa)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        idorden2 = sharedPreferences.getString("idOrden", "").toString()



        var latitud = getIntent().getStringExtra("latitud").toString()
        var longitud = getIntent().getStringExtra("longitud").toString()



        startService(Intent(this, LocationService::class.java))
        handler.postDelayed(object : Runnable {

            override fun run() {
                print("buscando")
                // función a ejecutar

                seguimiento()
                handler.postDelayed(this, 3000)
            }
        }, 2000)

        val apiInterface = Apis.create().getToken(idorden2)
        doAsync {
            apiInterface.enqueue(object : Callback<TokenClass> {
                override fun onResponse(
                    call: Call<TokenClass>,
                    response: Response<TokenClass>
                ) {
                    token = response.body()?.token.toString()
                }

                override fun onFailure(call: Call<TokenClass>, t: Throwable) {
                    Toast.makeText(this@OrdenActivaActivity, "error de token", Toast.LENGTH_LONG).show()
                }


            })
        }
        btnChat.setOnClickListener {

            val char = Intent(this, ChatActivity::class.java)
            char.putExtra("token", token)
            startActivity(char)
        }


        var btnMapa = findViewById<Button>(R.id.btnMapa)
        btnMapa.setOnClickListener {


            val googleMapsIntentUri = Uri.parse(
                "google.navigation:q=$latitud,$longitud"
            )

            val mapIntent = Intent(Intent.ACTION_VIEW, googleMapsIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            startActivity(mapIntent)

        }
        print("$idorden2")
        btnCancelar.setOnClickListener {
            val con = ConexionBD(this@OrdenActivaActivity)
            con.deleteData("chat")
            val dialog = AlertDialog.Builder(this)
                .setTitle("¿Desea confirmar esta ubicacion?")
                .setMessage("Esta apunto de poner su orden en marcha")
                .setNegativeButton("Cancelar") { view, _ ->
                    Toast.makeText(this, "Cancel button pressed", Toast.LENGTH_SHORT).show()
                    view.dismiss()
                }
                .setPositiveButton("Aceptar") { view, _ ->

                    val apiInterface = Apis.create().cancelarOrden(idorden2.toLong())
                    doAsync {
                        apiInterface.enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                AlertDialog.Builder(this@OrdenActivaActivity).apply {
                                    setTitle("Orden Cancelada")
                                    setMessage("Su Orden se cancelo correctamente")
                                    setNegativeButton("ok") { view, _ ->
                                        val sesion: SharedPreferences.Editor =
                                            sharedPreferences.edit()
                                        sesion.putString("idOrden", "")
                                        sesion.apply()
                                        val intent = Intent(
                                            this@OrdenActivaActivity,
                                            MenuActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    }
                                }.show()
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                AlertDialog.Builder(this@OrdenActivaActivity).apply {
                                    setTitle("Error en los datos")
                                    setMessage("¡Upss hubo un error en el envio! ${t}")
                                    setNegativeButton("ok", null)
                                }.show()
                            }

                        })
                    }

                }
                .setCancelable(false)
                .create()

            dialog.show()

        }

        btnIniciar.setOnClickListener {
            val sdf = SimpleDateFormat("hh:mm:ss")
            val currentDate = sdf.format(Date())
            System.out.println(" C DATE is  " + currentDate)

            var inicar = IniciarOrden(idorden2, currentDate)
            val apiInterface = Apis.create().iniciarOrden(inicar)
            doAsync {
                apiInterface.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        AlertDialog.Builder(this@OrdenActivaActivity).apply {
                            setTitle("Llego al destino")
                            setMessage("Se registro su hora de llegada")
                            setNegativeButton("ok", null)
                        }.show()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        AlertDialog.Builder(this@OrdenActivaActivity).apply {
                            setTitle("Error en los datos")
                            setMessage("¡Upss hubo un error en el envio! ${t}")
                            setNegativeButton("ok", null)
                        }.show()
                    }

                })
            }
        }

        btnFinalizar.setOnClickListener {
            val sdf = SimpleDateFormat("hh:mm:ss")
            val currentDate = sdf.format(Date())
            System.out.println(" C DATE is  " + currentDate)
            val con = ConexionBD(this@OrdenActivaActivity)
            con.deleteData("chat")
            var inicar = TerminarOrden(idorden2, currentDate)
            val apiInterface = Apis.create().finalizar(inicar)
            doAsync {
                apiInterface.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        //borrar esta linea en caso de error de ubicacion en vivo
                        handler.removeMessages(0)
                        AlertDialog.Builder(this@OrdenActivaActivity).apply {
                            setTitle("Orden Finalizada")
                            setMessage("¡Gracias por atender otra orden!")
                            setNegativeButton("ok") { view, _ ->
                                val sesion: SharedPreferences.Editor =
                                    sharedPreferences.edit()
                                sesion.putString("idOrden", "")
                                sesion.apply()

                                val cliente = Intent(this@OrdenActivaActivity, MenuActivity::class.java)
                                startActivity(cliente)
                                finish()
                            }
                        }.show()

                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        AlertDialog.Builder(this@OrdenActivaActivity).apply {
                            setTitle("Error en los datos")
                            setMessage("¡Upss hubo un error en el envio! ${t}")
                            setNegativeButton("ok", null)
                        }.show()
                    }

                })
            }
        }


    }

    fun seguimiento() {
        startService(Intent(this, LocationService::class.java))
        val sdf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())
        print("OrdenActiva: $idorden2")
        var obj = Trajecto(LocationService.loc.latitude, LocationService.loc.longitude, currentDate, idorden2, 3)

        val apiInterface = Apis.create().seguirUbicacion(obj)
        doAsync {
            if (apiInterface != null) {
                apiInterface.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        print("ubicacion enviada")

                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        print("ubicacion no se puedo enviar"+t.toString())
                    }


                })
            }
        }
    }

    fun onProviderEnabled(provider: String) {}

    fun onProviderDisabled(provider: String) {}

    fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

}