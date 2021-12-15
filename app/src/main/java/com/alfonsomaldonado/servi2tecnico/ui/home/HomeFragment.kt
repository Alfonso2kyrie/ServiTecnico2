package com.alfonsomaldonado.servi2tecnico.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alfonsomaldonado.servi2tecnico.*
import com.alfonsomaldonado.servi2tecnico.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.preference.PreferenceManager




class HomeFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    val handler = Handler()
    var idOrden = String()
    lateinit var idServidor: String
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = activity?.getSharedPreferences("sesion", Context.MODE_PRIVATE)!!
        idServidor = sharedPreferences.getString("idPersona", "").toString()


        val apiInterface = Apis.create().misOrdenes(idServidor.toString())
        doAsync {
            apiInterface.enqueue(object : Callback<List<Orden>> {
                override fun onResponse(call: Call<List<Orden>>, response: Response<List<Orden>>) {
                    val servicios = response?.body()
                    var miAdapter = OrdenAdapter(
                        activity!!,
                        R.layout.item_list_orden,
                        response.body()!!
                    )
                    listaOrdenes.adapter = miAdapter
                    miAdapter!!.notifyDataSetChanged()
                    listaOrdenes.setOnItemClickListener(
                        AdapterView.OnItemClickListener { parent, view, position, id ->

                            val dialog = activity?.let {
                                AlertDialog.Builder(it)
                                    .setTitle("¿Desea iniciar esta orden?")
                                    .setMessage("Esta apunto de poner su orden en marcha")
                                    .setNegativeButton("Cancelar") { view, _ ->

                                    }
                                    .setPositiveButton("Aceptar") { view, _ ->
                                        idOrden = response.body()!!.get(position).idOrden
                                        val activar = OrdenTecnico(
                                            response.body()!!.get(position).idOrden,
                                            idServidor.toString()
                                        )
                                        val apiInterface = Apis.create().asignarOrden(activar)
                                        doAsync {
                                            apiInterface.enqueue(object : Callback<Void> {
                                                override fun onResponse(
                                                    call: Call<Void>,
                                                    response: Response<Void>
                                                ) {
                                                    Toast.makeText(
                                                        activity,
                                                        "Orden Asignada",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    val sesion: SharedPreferences.Editor =
                                                        sharedPreferences.edit()
                                                    sesion.putString("idOrden", idOrden)
                                                    sesion.apply()

                                                    sharedPreferences = PreferenceManager
                                                        .getDefaultSharedPreferences(activity)
                                                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                                    editor.putString("key", idOrden)
                                                    editor.apply()

                                                }

                                                override fun onFailure(
                                                    call: Call<Void>,
                                                    t: Throwable
                                                ) {
                                                    AlertDialog.Builder(activity!!).apply {
                                                        setTitle("Error de sesion")
                                                        setMessage("¡Usuario o contraseña incorrecta!")
                                                        setNegativeButton("ok", null)
                                                    }.show()
                                                }

                                            })
                                        }

                                        val intent =
                                            Intent(
                                                activity,
                                                OrdenActivaActivity::class.java
                                            )
                                        intent.putExtra(
                                            "tipoServicio",
                                            response.body()!!.get(position).tipoServicio
                                        )
                                        intent.putExtra(
                                            "idServicio",
                                            response.body()!!.get(position).idOrden
                                        )
                                        intent.putExtra(
                                            "imagen",
                                            response.body()!!.get(position).imagenServicio
                                        )
                                        intent.putExtra(
                                            "latitud",
                                            response.body()!!.get(position).latitud
                                        )
                                        intent.putExtra(
                                            "longitud",
                                            response.body()!!.get(position).longitud
                                        )
                                        startActivity(intent)
                                        activity!!.finish()
                                    }
                                    .setCancelable(false)
                                    .create()
                            }

                            if (dialog != null) {
                                dialog.show()
                            }
                        })
                }

                override fun onFailure(call: Call<List<Orden>>, t: Throwable) {
                    activity?.let {
                        AlertDialog.Builder(it).apply {
                            setTitle("Error de sesion")
                            setMessage("¡Usuario o contraseña incorrecta!")
                            setNegativeButton("ok", null)
                        }.show()
                    }
                }

            })
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun buscar() {

    }
}