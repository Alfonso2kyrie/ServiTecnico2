package com.alfonsomaldonado.servi2tecnico.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.alfonsomaldonado.servi2tecnico.R

class AdapterChat (context: Context): BaseAdapter() {

    var mensajes = ArrayList<ChatModel>()
    var ctx = context

    fun add(mensaje: ChatModel){
        mensajes.add(mensaje)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mensajes.size
    }

    override fun getItem(p0: Int): Any {
        return mensajes.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var holder = MessageViewHolder()
        var myView = p1

        var messageInflater = LayoutInflater.from(ctx)
        var mensaje = mensajes.get(p0).message

        if(mensajes.get(p0).usuario.equals("Yo")){
            myView = messageInflater.inflate(R.layout.mimensaje, null)
            holder.cuerpoMensaje = myView.findViewById(R.id.cuerpoDelMensaje)

            holder.cuerpoMensaje!!.setText(mensaje)
        }else{
            myView = messageInflater.inflate(R.layout.sumnesaje, null)
            holder.cuerpoMensaje = myView.findViewById(R.id.cuerpoDelMensaje)

            holder.cuerpoMensaje!!.setText(mensaje)
        }
        return myView
    }

}

internal class MessageViewHolder{
    var cuerpoMensaje: TextView? = null
}