package com.example.myapplication.admin


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapplication.Carta.FragmentCarta
import com.example.myapplication.Evento.EventosFragment
import com.example.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
class Administrador : AppCompatActivity() {
    lateinit var navegation: BottomNavigationView

    private val mOnNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.administradorGestionarCartasFragment -> {
                supportFragmentManager.commit {
                    replace<FragmentCarta>(R.id.fragmentAdminCartas)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.administradorGestionarEventosFragment -> {
                supportFragmentManager.commit {
                    replace<FragmentAdminEventos>(R.id.fragmentAdminEventos) // Aquí se ha cambiado el ID
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.administradorGestionarVentasFragment -> {
                supportFragmentManager.commit {
                    replace<FragmentAdminPedidos>(R.id.fragmentAdminPedidos)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar?.hide()

        navegation = findViewById(R.id.bottomNavigationView)
        navegation.setOnNavigationItemSelectedListener(mOnNavMenu)

        supportFragmentManager.commit {
            replace<FragmentCarta>(R.id.fragmentAdminCartas)
            setReorderingAllowed(true)
            addToBackStack("replacement")
        }

    }
}