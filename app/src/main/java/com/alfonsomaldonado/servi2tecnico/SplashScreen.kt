package com.alfonsomaldonado.servi2tecnico

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class SplashScreen : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        var user = sharedPreferences.getString("username","").toString()
        var pass = sharedPreferences.getString("contrasena","").toString()



            startTimer(user, pass, )

    }

    fun startTimer(user:String,  pass:String){
        object : CountDownTimer(2000, 1000){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {

                if(user!= "" && pass!=""){

                        val sesion =
                            Intent(this@SplashScreen, MenuActivity::class.java).apply { }
                        startActivity(sesion)
                        finish()

                }else {

                    val instent = Intent(this@SplashScreen, MainActivity::class.java).apply { }
                    startActivity(instent)
                    finish()
                }
            }

        }.start()
    }
}