package com.alfonsomaldonado.servi2tecnico.utils
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alfonsomaldonado.servi2tecnico.MainActivity
import com.alfonsomaldonado.servi2tecnico.R
import com.alfonsomaldonado.servi2tecnico.chat.ChatActivity
import com.example.servi2.coneccion.ConexionBD

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            System.out.println("Mensaje 2" + remoteMessage.data.toString())
            if (true) {
                Log.d("FIREBASE", "Message data schedulejob: ${remoteMessage.data}")
                if (ChatActivity.chatActivo) {
                    enviarNotificacionLocal(
                        remoteMessage.data.get("message").toString(),
                        remoteMessage.data.get("token").toString()
                    )
                    saveData(remoteMessage.data.get("message").toString(), 1)

                } else {
                    enviarNotificacionLocal(
                        remoteMessage.data.get("message").toString(),
                        remoteMessage.data.get("token").toString()
                    )
                    saveData(remoteMessage.data.get("message").toString(), 0)

                    showNotication(
                        remoteMessage.data.get("message").toString(),
                        remoteMessage.data.get("token").toString()
                    )
                }
            }
        }else{
            Log.d("mensajeVacio", "mensaje viene vacio")
        }


        remoteMessage.notification?.let {
            System.out.println("Notificacion ->" + it.body.toString())
            showNotication(it.body.toString(), remoteMessage.data.get("token").toString())
        }

    }

    fun saveData(mensaje: String, leido: Int) {
        val con = ConexionBD(this)
        val datos = ContentValues()
        Log.d("Mensaje", datos.toString())

        datos.put("message", mensaje)
        datos.put("user", "El")
        datos.put("leido", leido)
        if (con.registerData("chat", datos)) {
            con.close()
        }
    }

    fun enviarNotificacionLocal(mensaje_que_llego: String, token: String) {
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        var FILTRO_CHAT = "broadcast_chat"
        val intent = Intent(FILTRO_CHAT)
        intent.putExtra("message", mensaje_que_llego)
        val sesion: SharedPreferences.Editor = sharedPreferences.edit()
        sesion.putString("remoteToken", token)
        sesion.apply()
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)


    }

    private fun sendRegistrationtoServer(token: String) {
        System.out.println("Envando token al web service ->" + token)

    }

    private fun showNotication(mensaje: String, token: String) {
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val intent = Intent(this, MainActivity::class.java)
        val sesion: SharedPreferences.Editor = sharedPreferences.edit()
        sesion.putString("remoteToken", token)
        sesion.apply()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Nuevo mensaje")
            .setContentText(mensaje)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //notificationManager.create
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())


    }
}