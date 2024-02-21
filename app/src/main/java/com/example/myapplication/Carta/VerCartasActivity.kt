package com.example.myapplication.Carta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityCartaVerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerCartasActivity : AppCompatActivity() {
    private lateinit var bind: ActivityCartaVerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityCartaVerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtén el ID del usuario actual
        val dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Usuarios").child(userId!!).child("Cartas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cardList = mutableListOf<Carta>()
                    snapshot.children.forEach { child: DataSnapshot? ->
                        val cardName = child?.getValue(String::class.java)
                        if (cardName != null) {
                            dbRef.child("Tienda").child("Cartas").orderByChild("nombreCarta").equalTo(cardName).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (cardSnapshot in snapshot.children) {
                                        val card = cardSnapshot.getValue(Carta::class.java)
                                        if (card != null) {
                                            cardList.add(card)
                                            val adapter = CartaCollectionAdaptador(cardList)
                                            bind.listaCartas.adapter = adapter
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        bind.volverInicioCartas.setOnClickListener {
            finish()
        }
    }
}