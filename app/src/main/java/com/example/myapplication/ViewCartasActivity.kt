package com.example.myapplication



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Carta.Carta
import com.google.firebase.database.*

class ViewCartasActivity : AppCompatActivity() {

//    private lateinit var recyclerView: RecyclerView
//    private lateinit var cartasAdapter: CartaAdaptador
//    private lateinit var cartasList: MutableList<Carta>
//    private lateinit var dbRef: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_recyclerview_cartas)
//
//        recyclerView = findViewById(R.id.rec)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        cartasList = mutableListOf()
//        cartasAdapter = CartaAdaptador(cartasList)
//        recyclerView.adapter = cartasAdapter
//
//        dbRef = FirebaseDatabase.getInstance().getReference("Tienda/Cartas")
//
//        dbRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                cartasList.clear()
//                for (cartaSnapshot in dataSnapshot.children) {
//                    val carta = cartaSnapshot.getValue(Carta::class.java)
//                    if (carta != null) {
//                        cartasList.add(carta)
//                    }
//                }
//                cartasAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle possible errors.
//            }
//        })
//    }
}