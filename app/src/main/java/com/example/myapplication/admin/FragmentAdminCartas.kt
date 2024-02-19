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
import com.example.myapplication.Carta.Carta
import com.example.myapplication.Carta.CrearCarta
import com.example.myapplication.Carta.VerCartas
import com.example.myapplication.CartaAdaptador
import com.example.myapplication.R
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
 * Use the [FragmentAdminCartas.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentAdminCartas : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_cartas, container, false)

        // Initialize the RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.lista_carta)

        // Initialize the Button
        val addButton = view.findViewById<Button>(R.id.add_carta)

        // Set up the Button
        addButton.setOnClickListener {
            val intent = Intent(context, CrearCarta::class.java)
            startActivity(intent)
        }
        // Create an empty mutable list of Carta
        val cartasList = mutableListOf<Carta>()
        // Get a reference to the cartas in Firebase
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("Tienda").child("Cartas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Add a ValueEventListener to the database reference
                    snapshot.children.forEach { hijo: DataSnapshot? ->
                        val pojo_carta = hijo?.getValue(Carta::class.java)
                        cartasList.add(pojo_carta!!)
                    }
                    // Set up the RecyclerView after data has been added to the list
                    recyclerView.adapter = CartaAdaptador(cartasList)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        // Add a ValueEventListener to the database reference

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentAdminCartas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentAdminCartas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}