package com.example.myapplication.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Evento.AddEventoFragment

import com.example.myapplication.Evento.Evento
import com.example.myapplication.Evento.EventoAdaptador
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentAdminEventos : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_evento, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.sc_eventos)
        val addButton = view.findViewById<Button>(R.id.btn_agregar_evento)

        addButton.setOnClickListener {
            val fragment = AddEventoFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
            requireActivity().supportFragmentManager.popBackStack()
        }

        val eventosList = mutableListOf<Evento>()
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("Tienda").child("Eventos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { hijo: DataSnapshot? ->
                        val pojo_evento = hijo?.getValue(Evento::class.java)
                        eventosList.add(pojo_evento!!)
                    }
                    recyclerView.adapter = EventoAdaptador(eventosList)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        return view
    }
}