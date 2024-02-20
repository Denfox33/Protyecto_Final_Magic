package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Carta.Carta
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ShopFragmentUser : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_user, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.sc_cartas)
        val cartasList = mutableListOf<Carta>()

        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("Tienda").child("Cartas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { hijo: DataSnapshot? ->
                        val pojo_carta = hijo?.getValue(Carta::class.java)
                        if (pojo_carta != null && pojo_carta.disponibilidad == "Si") {
                            cartasList.add(pojo_carta)
                        }
                    }
                    recyclerView.adapter = CartaAdaptador(cartasList)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShopFragmentUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}