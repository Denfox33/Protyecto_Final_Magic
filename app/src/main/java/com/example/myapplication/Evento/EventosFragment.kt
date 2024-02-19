package com.example.myapplication.Evento

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Carta.Utilidades
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEventoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventosFragment : Fragment() {
    private lateinit var bind: FragmentEventoBinding
    private lateinit var lista: MutableList<Evento>
    private lateinit var db_ref: DatabaseReference
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: EventoAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = FragmentEventoBinding.inflate(layoutInflater)
        db_ref = FirebaseDatabase.getInstance().reference
        lista = mutableListOf()

        db_ref.child("Tienda").child("Eventos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoevento = hijo!!.getValue(Evento::class.java)

                    val userEmail = FirebaseAuth.getInstance().currentUser?.email
                    val isAdmin = userEmail?.endsWith("@admin.com") ?: false
                    if (isAdmin) {
                        lista.add(pojoevento!!)
                    } else {
                        if (pojoevento?.aforoactual!! < pojoevento?.aforomax!!){
                            lista.add(pojoevento)
                        }
                    }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        adaptador = EventoAdaptador(lista)
        recycler = bind.scEventos
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext@ context)
        recycler.setHasFixedSize(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        val isAdmin = userEmail?.endsWith("@admin.com") ?: false
        if (isAdmin) {
            bind.btnAgregarEvento.visibility = View.VISIBLE
        } else {
            bind.btnAgregarEvento.visibility = View.GONE
        }

        return bind.root
    }

    override fun onStart() {
        super.onStart()
        bind.btnAgregarEvento.setOnClickListener {
            val fragment = AddEventoFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        bind.lupa.setOnClickListener {
            mostrarBusqueda()
        }

        bind.close.setOnClickListener {
            ocultarBusqueda()
        }

        bind.buscarEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textoIngresado = s.toString()
                adaptador.filter.filter((textoIngresado))
            }

            override fun afterTextChanged(p0: Editable?) {
                null
            }

        })

        bind.opciones.setOnClickListener {
            Utilidades.showPopupMenuOptions(it, requireContext())
        }
    }

    fun mostrarBusqueda() {
        bind.close.visibility = View.VISIBLE
        bind.lupa.visibility = View.INVISIBLE
        bind.buscarEt.visibility = View.VISIBLE
        bind.opciones.visibility = View.INVISIBLE
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(bind.buscarEt, InputMethodManager.SHOW_IMPLICIT)
    }

    fun ocultarBusqueda() {
        bind.close.visibility = View.INVISIBLE
        bind.lupa.visibility = View.VISIBLE
        bind.buscarEt.visibility = View.INVISIBLE
        bind.opciones.visibility = View.VISIBLE
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(bind.buscarEt.windowToken, 0)
        bind.buscarEt.setText("")
    }
}