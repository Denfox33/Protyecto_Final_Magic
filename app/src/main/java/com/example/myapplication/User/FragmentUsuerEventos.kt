package com.example.myapplication.User

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Evento.Evento
import com.example.myapplication.Evento.EventoAdaptador
import com.example.myapplication.databinding.FragmentUsuerEventosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentUsuerEventos : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: MutableList<Evento>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adapter: EventoAdaptador

    private var _binding: FragmentUsuerEventosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsuerEventosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtén el ID del usuario actual

        list = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("Usuarios").child(userId!!).child("Eventos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach { child: DataSnapshot? ->
                        val eventId = child?.getValue(String::class.java)
                        if (eventId != null) {
                            db_ref.child("Tienda").child("Eventos").child(eventId).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val event = snapshot.getValue(Evento::class.java)
                                    if (event != null) {
                                        list.add(event)
                                    }
                                    recyclerView.adapter?.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    println(error.message)
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        recyclerView = binding.scEventos
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = EventoAdaptador(list)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}