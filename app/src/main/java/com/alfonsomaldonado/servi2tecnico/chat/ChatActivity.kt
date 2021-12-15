package com.alfonsomaldonado.servi2tecnico.chat

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alfonsomaldonado.servi2tecnico.Apis
import com.alfonsomaldonado.servi2tecnico.R
import com.example.servi2.coneccion.ConexionBD
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    var mensajes_chat: ArrayList<ChatModel> = ArrayList<ChatModel>()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var messageAdapter: AdapterChat
    private val FILTRO_CHAT = "broadcast_chat"

    var flag = true

    companion object {
        var chatActivo = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatActivo = true

        messageAdapter = AdapterChat(this)
        chtList.adapter = messageAdapter
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE)
        var tokenTecnico = intent.getStringExtra("token").toString()
        btnSendMessage.setOnClickListener {

            sendMensaje(mensaje.text.toString(), "Yo")
            saveData(mensaje.text.toString())
            val token = sharedPreferences.getString("remoteToken","#")
            val newMessage = MessageModel(tokenTecnico,mensaje.text.toString())

            val apiInterface = Apis.create().envio(newMessage)

            if (apiInterface != null) {
                doAsync {
                    apiInterface.enqueue(object : Callback<MessageModel> {
                        override fun onResponse(
                            call: Call<MessageModel>?,
                            response: Response<MessageModel>?
                        ) {

                        }

                        override fun onFailure(call: Call<MessageModel>?, t: Throwable?) {
                            Log.d("Failure", t.toString())
                        }
                    })
                }
            }
            println("Mensaje Save"+ mensaje.text)
            mensaje.setText("")

        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcast, IntentFilter(FILTRO_CHAT))

        getMensajes()
    }

    val broadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            sendMensaje(intent!!.getStringExtra("message").toString(), "El")
        }
    }

    fun sendMensaje(mensaje_str: String, tipo: String) {
        mensajes_chat.add(ChatModel(mensaje_str, tipo))
        messageAdapter.add(ChatModel(mensaje_str, tipo))
        //messageAdapter.notifyDataSetChanged()
        chtList.setSelection(messageAdapter.count - 1)



    }

    fun saveData(mensaje: String) {
        val con = ConexionBD(this)
        val datos = ContentValues()
        println("Mensaje Save"+ mensaje)
        datos.put("message", mensaje)
        datos.put("user", "Yo")
        datos.put("leido", 1)
        if (con.registerData("chat", datos)) {
            con.close()
        }
    }

    fun getMensajes() {
        try {
            val con = ConexionBD(this)
            var datos = con.getData("chat", arrayOf("id", "user", "message"))
            while (datos.moveToNext()) {
                println("Mensaje "+datos.getString(2))
                messageAdapter.add(ChatModel(datos.getString(2), datos.getString(1)))
            }
            messageAdapter.notifyDataSetChanged()
            chtList.setSelection(messageAdapter.count - 1)
            datos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        chatActivo = true
    }

    override fun onStop() {
        super.onStop()
        chatActivo = false
    }

}