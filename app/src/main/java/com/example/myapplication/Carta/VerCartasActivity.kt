package com.example.myapplication.Carta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityCartaVerBinding
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

        val userId = intent.getStringExtra("userId")
        val dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Tienda").child("Cartas").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cardList = mutableListOf<Carta>()
                for (childSnapshot in snapshot.children) {
                    val card = childSnapshot.getValue(Carta::class.java)
                    if (card != null) {
                        dbRef.child("Usuarios").child(userId!!).child("Cartas").orderByValue().equalTo(card.nombreCarta).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    cardList.add(card)
                                    val adapter = CartaCollectionAdaptador(cardList)
                                    bind.listaCartas.adapter = adapter
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