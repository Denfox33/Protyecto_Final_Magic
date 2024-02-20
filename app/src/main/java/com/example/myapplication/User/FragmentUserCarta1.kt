package com.example.myapplication.User

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Carta.Carta
import com.example.myapplication.Carta.CartaCollectionAdaptador
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentUserCarta1.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentUserCarta1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_recyclerview_cartas, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.lista_cartas1)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!).child("Cartas")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartasList = mutableListOf<Carta>()
                for (cartaSnapshot in snapshot.children) {
                    val carta = cartaSnapshot.getValue(Carta::class.java)
                    if (carta != null) {
                        cartasList.add(carta)
                    }
                }
                recyclerView.adapter = CartaCollectionAdaptador(cartasList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        return view
    }
}