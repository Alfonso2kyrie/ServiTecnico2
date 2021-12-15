package com.alfonsomaldonado.servi2tecnico

import android.service.autofill.UserData
import com.alfonsomaldonado.servi2tecnico.chat.MessageModel
import com.alfonsomaldonado.servi2tecnico.chat.TokenClass
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


interface Apis {
    @GET("/test")
    fun listaUsuario(): ArrayList<Usuario>

    @POST("usuario/create")
    fun createUser(@Body Usuario: Usuario?): Call<Usuario>?

    @POST("login")
    fun iniciarSesion(@Body usuario:Login?): Call<Usuario>?

    @PUT("usuario/update")
    fun actualizarUsuario(@Body usuario: Perfil): Call<Void>

    @GET("orden/tome/{id}")
    fun misOrdenes(@Path("id")id:String):Call<List<Orden>>

    @PUT("orden/asignar")
    fun asignarOrden(@Body activar:OrdenTecnico):Call<Void>

    @PUT("orden/cancelar/{id}")
    fun cancelarOrden(@Path("id")idOrden: Long):Call<Void>

    @PUT("orden/iniciar")
    fun iniciarOrden(@Body inicar:IniciarOrden):Call<Void>

    @PUT("orden/terminar")
    fun finalizar(@Body horaFin:TerminarOrden):Call<Void>

    @POST("orden/save/ubicacion")
    fun seguirUbicacion(@Body trajecto:Trajecto): Call<Void>

    @POST("usuario/create/conductor")
    fun guardarConductor(@Body conductor: Conductor):Call<idPersona>

    @GET("servicios")
    fun getServicios(): Call<ArrayList<Servicio>>

    @POST("misServicios/save")
    fun asignarServicio(@Body miServicio:miServicio):Call<Void>

    @POST("sendMessage")
    fun envio(@Body message: MessageModel): Call<MessageModel>

    @GET("orden/token/{id}")
    fun getToken(@Path("id")id:String):Call<TokenClass>

    companion object {

        var BASE_URL = "http://192.168.0.24:3000/"

        fun create() : Apis {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(Apis::class.java)

        }
    }

}

/*private var retrofit = Retrofit.Builder()
    .baseUrl("http://localhost:3000/")
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

var service: Apis = retrofit.create(Apis::class.java)*/