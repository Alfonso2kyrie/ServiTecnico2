package com.alfonsomaldonado.servi2tecnico

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var user = findViewById<Button>(R.id.txtUsuario) as EditText
        var contra = findViewById<Button>(R.id.txtContrasena) as EditText


        val btnInicio = findViewById<Button>(R.id.btnLogin)
        btnInicio.setOnClickListener {
            var login = Login(user.text.toString(),contra.text.toString())
            val apiInterface = Apis.create().iniciarSesion(login)
            doAsync {
                if (apiInterface != null) {
                    apiInterface.enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            val usuario = response?.body()
                            val sesion: SharedPreferences.Editor = sharedPreferences.edit()
                            sesion.putString("idPersona", usuario?.idPersona)
                            System.out.println("usuariooooooooooooooo " + usuario?.idPersona)
                            sesion.putString("nombre", usuario?.nombre)
                            sesion.putString("ape1", usuario?.apellidoPaterno)
                            sesion.putString("ape2", usuario?.apellidosMaterno)
                            sesion.putString("email", usuario?.email)
                            sesion.putString("telefono", usuario?.telefono)
                            sesion.putString("username", usuario?.username)
                            sesion.putString("contrasena", usuario?.contrasena)
                            sesion.putInt("rol", usuario?.rol_idRol!!)
                            sesion.apply()
                            Toast.makeText(
                                applicationContext,
                                usuario?.username.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            if(usuario.rol_idRol!=1){
                                val cliente = Intent(this@MainActivity, MenuActivity::class.java)
                                startActivity(cliente)
                                finish()
                            }else{
                                AlertDialog.Builder(this@MainActivity).apply {
                                    setTitle("Error de sesion")
                                    setMessage("Usuario no valido")
                                    setNegativeButton("ok", null)
                                }.show()
                            }
                        }

                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Log.e("errorrrrrrrrrr", t.toString())
                            AlertDialog.Builder(this@MainActivity).apply {
                                setTitle("Error de sesion")
                                setMessage("¡Usuario o contraseña incorrecta!")
                                setNegativeButton("ok", null)
                            }.show()
                            Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }


                    })
                }
            }
        }


        btnCrear.setOnClickListener {
            val intent = Intent(this, CrearActivity::class.java)
            startActivity(intent)
        }
    }
}