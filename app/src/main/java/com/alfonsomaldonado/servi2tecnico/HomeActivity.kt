package com.alfonsomaldonado.servi2tecnico

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    val handler = Handler()
    var idOrden = String()
    lateinit var idServidor:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        idServidor = sharedPreferences.getString("idPersona", "").toString()
        handler.postDelayed(object : Runnable {
            override fun run() {
                print("buscando")
                buscar()
                handler.postDelayed(this, 10000)
            }
        }, 2000)
    }

    fun buscar (){
        val apiInterface = Apis.create().misOrdenes(idServidor.toString())
        doAsync {
            apiInterface.enqueue(object : Callback<List<Orden>> {
                override fun onResponse(call: Call<List<Orden>>, response: Response<List<Orden>>) {
                    val servicios = response?.body()
                    var miAdapter =
                        OrdenAdapter(this@HomeActivity, R.layout.item_list_orden, response.body()!!)
                    listaOrdenes.adapter = miAdapter
                    miAdapter!!.notifyDataSetChanged()
                    listaOrdenes.setOnItemClickListener(
                        AdapterView.OnItemClickListener { parent, view, position, id ->

                            val dialog = AlertDialog.Builder(this@HomeActivity)
                                .setTitle("¿Desea iniciar esta orden?")
                                .setMessage("Esta apunto de poner su orden en marcha")
                                .setNegativeButton("Cancelar") { view, _ ->

                                }
                                .setPositiveButton("Aceptar") { view, _ ->
                                    idOrden = response.body()!!.get(position).idOrden
                                    val activar = OrdenTecnico(
                                        response.body()!!.get(position).idOrden,
                                        idServidor.toString()
                                    )
                                    val apiInterface = Apis.create().asignarOrden(activar)
                                    doAsync {
                                        apiInterface.enqueue(object : Callback<Void> {
                                            override fun onResponse(
                                                call: Call<Void>,
                                                response: Response<Void>
                                            ) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Orden Asignada",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                val sesion: SharedPreferences.Editor = sharedPreferences.edit()
                                                sesion.putString("idOrden", idOrden)
                                                sesion.apply()

                                            }

                                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                                AlertDialog.Builder(this@HomeActivity).apply {
                                                    setTitle("Error de sesion")
                                                    setMessage("¡Usuario o contraseña incorrecta!")
                                                    setNegativeButton("ok", null)
                                                }.show()
                                            }

                                        })
                                    }

                                    val intent =
                                        Intent(this@HomeActivity, OrdenActivaActivity::class.java)
                                    intent.putExtra(
                                        "tipoServicio",
                                        response.body()!!.get(position).tipoServicio
                                    )
                                    intent.putExtra(
                                        "idServicio",
                                        response.body()!!.get(position).idOrden
                                    )
                                    intent.putExtra(
                                        "imagen",
                                        response.body()!!.get(position).imagenServicio
                                    )
                                    intent.putExtra(
                                        "latitud",
                                        response.body()!!.get(position).latitud
                                    )
                                    intent.putExtra(
                                        "longitud",
                                        response.body()!!.get(position).longitud
                                    )
                                    startActivity(intent)

                                }
                                .setCancelable(false)
                                .create()

                            dialog.show()
                        })
                }

                override fun onFailure(call: Call<List<Orden>>, t: Throwable) {
                    AlertDialog.Builder(this@HomeActivity).apply {
                        setTitle("Error de sesion")
                        setMessage("¡Usuario o contraseña incorrecta!")
                        setNegativeButton("ok", null)
                    }.show()
                }

            })
        }
    }
}