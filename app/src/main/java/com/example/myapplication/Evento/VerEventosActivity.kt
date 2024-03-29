package com.example.myapplication.Evento

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityEventosVerUsuarioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerEventosActivity : AppCompatActivity() {
    private lateinit var bind: ActivityEventosVerUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("VerEventosActivity", "onCreate() called")
        bind = ActivityEventosVerUsuarioBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val userId = intent.getStringExtra("userId")
        Log.v("VerEventosActivity", "userId: $userId")
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("Usuarios").child(userId!!).child("Eventos").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.v("VerEventosActivity", "onDataChange() called")
                val eventList = mutableListOf<Evento>()
                for (childSnapshot in snapshot.children) {
                    val event = childSnapshot.getValue(Evento::class.java)
                    if (event != null) {
                        eventList.add(event)
                    }
                }

                // Display the event collection using the EventCollectionAdaptador
                val adapter = EventCollectionAdaptador(eventList)
                Log.v("VerEventosActivity", "Adapter created with ${eventList.size} events")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.v("VerEventosActivity", "onCancelled() called: ${error.message}")
            }


        })
        bind.regresar.setOnClickListener {
            finish()
        }
    }

}