package com.example.myapplication.Registro

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.User.FragmentUsuarioConfi
import com.example.myapplication.admin.FragmentAdminConfi
import com.example.myapplication.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val currentTheme = sharedPref.getInt("current_theme", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(currentTheme)

        initUI()
    }

    private fun initUI() {
        // Inicializa cualquier otro componente de UI que necesites
        initNavigation(intent)
    }

    private fun initNavigation(intent: Intent?) {
        val isAdmin = intent?.getBooleanExtra("isAdmin", false) ?: false
        val navGraphId = if (isAdmin) R.navigation.nav_admin else R.navigation.nav_graph

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (navHostFragment is NavHostFragment) {
            val navController = navHostFragment.navController
            navController.graph = navController.navInflater.inflate(navGraphId)

            // Establecer el menú
            val menuResId = if (isAdmin) R.menu.menu_bottom_admin else R.menu.menu_bottom_menu
            binding.bottomNavigationView1.setupWithNavController(navController)
            binding.bottomNavigationView1.menu.clear()
            binding.bottomNavigationView1.inflateMenu(menuResId)

            // Manejar la navegación específica del administrador
            if (isAdmin) {
                handleAdminNavigation(navController)
            } else {
                handleUserNavigation(navController)
            }
        } else {
            throw RuntimeException("The fragment with id nav_host_fragment is not a NavHostFragment")
        }
    }

    private fun handleAdminNavigation(navController: NavController) {
        binding.bottomNavigationView1.setOnNavigationItemSelectedListener { item ->
            Log.v("MainActivity", "Item: ${item}")
            when (item.itemId) {
                R.id.administradorHomeFragment -> {
                    Log.v("MainActivity", "Home")
                    navController.navigate(R.id.mainAdminFragment)
                    true
                }
                R.id.administradorGestionarVentasFragment -> {
                    Log.v("MainActivity", "Gestionar ventas")
                    navController.navigate(R.id.fragmentAdminPedidos)
                    true
                }

                R.id.administradorGestionarCartasFragment -> {
                    Log.v("MainActivity", "Gestionar cartas")
                    navController.navigate(R.id.fragmentAdminCartas)
                    true
                }

                R.id.administradorGestionarEventosFragment -> {
                    Log.v("MainActivity", "Gestionar eventos")
                    navController.navigate(R.id.fragmentAdminEventos)
                    true
                }

                R.id.AdminConfiguracion -> {
                    Log.v("MainActivity", "Configuración")
                    navController.navigate(R.id.fragmentAdminConfi)
                    true
                }

                else -> false
            }
        }
    }

    private fun handleUserNavigation(navController: NavController) {
        binding.bottomNavigationView1.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainFragmentUser1 -> {
                    navController.navigate(R.id.infoUserProfile)
                    true
                }
                R.id.shopFragmentUser1 -> {
                    navController.navigate(R.id.fragmentUserPedidos)
                    true
                }



                R.id.cartasUsuario -> {
                    navController.navigate(R.id.fragmetUserCartas1)
                    true
                }

                R.id.eventosUsaurio -> {
                    // Iniciar el fragmento de configuración según el rol
                    navController.navigate(R.id.evnetosUsuario)
                    true
                }

                R.id.profileFragmentUser1 -> {
                    Log.e("MainActivity", "")
                    navController.navigate(R.id.mainFragmentUser)
                    true
                }

                else -> false
            }
        }
    }
}