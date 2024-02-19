package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.Carta.Utilidades
import com.example.myapplication.Registro.LoginActivity
import com.example.myapplication.databinding.FragmentUsuarioUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class MainFragmentUser : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var bind: FragmentUsuarioUserBinding
    private lateinit var db_ref :DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentUsuarioUserBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db_ref = FirebaseDatabase.getInstance().reference

        launch {
            val userId = FirebaseAuth.getInstance().uid.toString()
            val userSnapshot = db_ref.child("Shop").child("Users").child(userId).get().await()
            val user = userSnapshot.getValue(Usuario::class.java)
            if (user != null) {
                bind.textName.text = user.name
                bind.textEmail.text = user.email
                bind.textDate.text = user.date
                if (user.url_firebase != null && user.url_firebase != "") {
                    Glide.with(requireContext())
                        .load(user.url_firebase)
                        .apply(Utilidades.opcionesGlide(requireContext()))
                        .transition(Utilidades.transicion)
                        .into(bind.profileImage)
                } else {
                    bind.profileImage.setImageResource(R.drawable.cartadef)
                }
            }
        }

        bind.btnViewCards.setOnClickListener {
            // Aquí puedes agregar el código para mostrar las cartas de la colección del cliente
        }

        bind.btnViewEvents.setOnClickListener {
            // Aquí puedes agregar el código para mostrar los eventos a los que se ha apuntado el cliente
        }

        bind.btnLogout.setOnClickListener {
            // Cerrar la sesión del usuario actual
            FirebaseAuth.getInstance().signOut()

            // Navegar de nuevo a la actividad de inicio de sesión
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}