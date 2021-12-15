package com.alfonsomaldonado.servi2tecnico

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_crear.*
import kotlinx.android.synthetic.main.activity_main.*

class CrearActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear)
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)

        btnCrearCuenta.setOnClickListener {
            var nombre = txtNombre.text.toString()
            var ape1 = txtApe1.text.toString()
            var ape2 = txtApe2.text.toString()
            var email = txtEmail.text.toString()
            var telefono = txtTelefonso.text.toString()
            var username = txtUsername.text.toString()
            var contra = txtContrasena2.text.toString()

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
                if(it.isSuccessful){
                    val token = it.result
                    println("Firebase token -> $token")
                    val sesion: SharedPreferences.Editor =
                        sharedPreferences.edit()
                    sesion.putString("token", token.toString())
                    sesion.apply()
                    return@OnCompleteListener
                }
            })


            val serv = Intent(this, ConductorActivity::class.java)
            serv.putExtra("nombre", nombre)
            serv.putExtra("ape1", ape1)
            serv.putExtra("ape2", ape2)
            serv.putExtra("email", email)
            serv.putExtra("telefono", telefono)
            serv.putExtra("username", username)
            serv.putExtra("contra", contra)
            startActivity(serv)
            finish()
        }


    }
}