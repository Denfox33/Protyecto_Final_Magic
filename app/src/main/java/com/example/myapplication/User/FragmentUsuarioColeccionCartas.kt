package com.example.myapplication.Userpackage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Carta.Carta
import com.example.myapplication.CartaAdaptador
import com.example.myapplication.databinding.FragmentUsuarioColeccionCartasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentUsuarioColeccionCartas : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var list: MutableList<Carta>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adapter: CartaAdaptador

    private var _binding: FragmentUsuarioColeccionCartasBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsuarioColeccionCartasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtén el ID del usuario actual

        list = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("Usuarios").child(userId!!).child("Cartas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach { child: DataSnapshot? ->
                        val cardName = child?.getValue(String::class.java)
                        if (cardName != null) {
                            db_ref.child("Tienda").child("Cartas").orderByChild("nombreCarta").equalTo(cardName).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (cardSnapshot in snapshot.children) {
                                        val card = cardSnapshot.getValue(Carta::class.java)
                                        if (card != null) {
                                            list.add(card)
                                        }
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

        recyclerView = binding.listaCartas1
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = CartaAdaptador(list)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic fun newInstance(param1: String, param2: String) =
            FragmentUsuarioColeccionCartas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}