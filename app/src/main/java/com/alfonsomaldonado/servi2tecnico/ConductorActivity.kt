package com.alfonsomaldonado.servi2tecnico

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_conductor.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConductorActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conductor)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        var nombre = intent.getStringExtra("nombre").toString()
        var ape1 = intent.getStringExtra("ape1").toString()
        var ape2 = intent.getStringExtra("ape2").toString()
        var gmail = intent.getStringExtra("email").toString()
        var telefono = intent.getStringExtra("telefono").toString()
        var username = intent.getStringExtra("username").toString()
        var contra = intent.getStringExtra("contra").toString()
        var tokensiwis = sharedPreferences.getString("token","").toString()
        btnRegistrar.setOnClickListener {
            Log.d("flujo", "1")
            var modelo = txtModelo.text.toString()
            var color = txtColor.text.toString()

            var conductor = Conductor(nombre, ape1, ape2, gmail, telefono, username, contra, modelo, color, tokensiwis)
            Log.d("flujo", "2")
            val apiInterface = Apis.create().guardarConductor(conductor)
            doAsync {
                apiInterface.enqueue(object : Callback<idPersona> {
                    override fun onResponse(call: Call<idPersona>, response: Response<idPersona>) {
                        Log.d("flujo", "3")
                        Log.d("idUser", response.body()?.lastID.toString())
                        val sesion: SharedPreferences.Editor = sharedPreferences.edit()
                        sesion.putString("idPersona", response.body()?.lastID.toString())
                        sesion.apply()
                        AlertDialog.Builder(this@ConductorActivity).apply {
                            setTitle("usuario Creado")
                            setMessage("¡Su cuenta esta lista!")
                            setNegativeButton("ok") { view, _ ->
                                val cliente = Intent(this@ConductorActivity, MisServiciosActivity::class.java)
                                startActivity(cliente)
                                finish()
                            }
                        }.show()
                    }

                    override fun onFailure(call: Call<idPersona>, t: Throwable) {
                        Log.d("errorConductor", t.message.toString())
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                })
            }

        }

    }
}