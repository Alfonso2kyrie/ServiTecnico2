package com.alfonsomaldonado.servi2tecnico

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ServicioAdapter (private var context: Context, private var layout: Int, private var dataSource: List<Servicio>) : BaseAdapter() {

    private  val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflater.inflate(layout,p2,false)

        val servicio = view.findViewById<TextView>(R.id.txtServicio)
        val imageView = view.findViewById<ImageView>(R.id.imServicio)
        val idServicio = view.findViewById<TextView>(R.id.txtIdServicio)


        val element = getItem(p0) as Servicio

        servicio.text= element.tipoServicio
        idServicio.text = element.idServicio
        Picasso.get().load(element.imagenServicio).into(imageView)


        return view
    }

    override fun getItem(p0: Int): Any {
        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

}
