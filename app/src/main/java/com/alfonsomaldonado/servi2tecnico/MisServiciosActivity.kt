package com.alfonsomaldonado.servi2tecnico

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_mis_servicios.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MisServiciosActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    var misServicion: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_servicios)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)

        var idPersona = sharedPreferences.getString("idPersona", "").toString()

        val apiInterface = Apis.create().getServicios()

        if (apiInterface != null) {
            doAsync {
                apiInterface.enqueue(object : Callback<ArrayList<Servicio>> {
                    override fun onResponse(
                        call: Call<ArrayList<Servicio>>,
                        response: Response<ArrayList<Servicio>>
                    ) {
                        var miAdapter = ServicioAdapter(
                            this@MisServiciosActivity,
                            R.layout.item_list_orden,
                            response.body()!!
                        )
                        listaMisServicios.adapter = miAdapter
                        miAdapter!!.notifyDataSetChanged()
                        listaMisServicios.setOnItemClickListener(
                            AdapterView.OnItemClickListener { parent, view, position, id ->

                                Log.d(
                                    "iddddddddddddddd",
                                    response.body()!!.get(position).idServicio
                                )
                                misServicion.add(response.body()!!.get(position).idServicio)
                                AlertDialog.Builder(this@MisServiciosActivity).apply {
                                    setTitle("Servicio Seleccionado")
                                    setMessage("¡se a añadido a la cola!")
                                    setNegativeButton("ok") { view, _ ->

                                    }
                                }.show()
                            })
                    }

                    override fun onFailure(call: Call<ArrayList<Servicio>>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }


                })
            }
        }

        btnListo.setOnClickListener {
            for (i in misServicion) {
                val obj = miServicio(i, idPersona)
                val apiInterface = Apis.create().asignarServicio(obj)
                doAsync {
                    apiInterface.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            print("listo")
                            val login = Intent(this@MisServiciosActivity, MainActivity::class.java)
                            startActivity(login)
                            finish()

                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                t.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    })
                }
            }
            Toast.makeText(
                applicationContext,
                "Servicios Asignados",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}