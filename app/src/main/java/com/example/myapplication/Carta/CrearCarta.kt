package com.example.myapplication.Carta



import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.example.myapplication.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class CrearCarta : AppCompatActivity(), CoroutineScope {

    private lateinit var nombreCarta: EditText
    private lateinit var precio: EditText
    private lateinit var stock: EditText
    private lateinit var disponibilidad: AutoCompleteTextView
    private lateinit var color: AutoCompleteTextView
    private lateinit var icono: ImageView
    private lateinit var crear: AppCompatButton
    private lateinit var volver: AppCompatButton

    private var urlCarta: Uri? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var stoRef: StorageReference
    private lateinit var listaCartas: MutableList<Carta>

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carta_crear)

        val thisActivity = this
        job = Job()

        nombreCarta = findViewById(R.id.tietNombre)
        precio = findViewById(R.id.tietPrecio)
        stock = findViewById(R.id.tietStock)
        disponibilidad = findViewById(R.id.tetDisponible)
        color = findViewById(R.id.tetColor)
        icono = findViewById(R.id.ivImagen)
        crear = findViewById(R.id.btnAgregarCarta)
        volver = findViewById(R.id.Volver)

        dbRef = FirebaseDatabase.getInstance().reference
        stoRef = FirebaseStorage.getInstance().reference
        listaCartas = Utilidades.obtenerListaCartas(dbRef)

        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = formatoFecha.format(Date())

        crear.setOnClickListener {
            Log.v("CrearCarta", "Botón crear presionado")
            val nombreCartaTexto = nombreCarta.text.toString().trim()
            val precioTexto = precio.text.toString().trim()
            val stockTexto = stock.text.toString().trim()
            val disponibilidadTexto = disponibilidad.text.toString().trim()
            val colorTexto = color.text.toString().trim()

            if (nombreCartaTexto.isEmpty() || precioTexto.isEmpty() || stockTexto.isEmpty() ||
                disponibilidadTexto.isEmpty() || colorTexto.isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()
                Log.v("CrearCarta", "Faltan datos en el formulario")
            } else if (urlCarta == null) {
                Toast.makeText(
                    applicationContext, "Falta seleccionar el " +
                            "icono", Toast.LENGTH_SHORT
                ).show()
                Log.v("CrearCarta", "Falta seleccionar el icono")
            } else if (Utilidades.existeCarta(listaCartas, nombreCartaTexto)) {
                Toast.makeText(applicationContext, "Esa carta ya existe", Toast.LENGTH_SHORT)
                    .show()
                Log.v("CrearCarta", "Esa carta ya existe")
            } else {
                var idGenerado: String? = dbRef.child("Tienda").child("Cartas").push().key
                Log.v("CrearCarta", "ID generado: $idGenerado")
                launch {
                    Log.v("CrearCarta", "Iniciando corrutina")
                    val urlCartaFirebase =
                        Utilidades.guardarIcono(stoRef, idGenerado!!, urlCarta!!)
                    Log.v("CrearCarta", "Icono guardado: $urlCartaFirebase")
                    Utilidades.registrarCartaEnBaseDatos(
                        dbRef, idGenerado!!,
                        nombreCartaTexto,
                        precioTexto.toDouble(),
                        stockTexto.toInt(),
                        disponibilidadTexto,
                        colorTexto,
                        urlCartaFirebase
                    )
                    Log.v("CrearCarta", "Carta registrada en base de datos")
                    Utilidades.tostadaCorrutina(
                        thisActivity,
                        applicationContext,
                        "Carta creada con éxito"
                    )
                    Log.v("CrearCarta", "Carta creada con éxito")
                    Log.v("CrearCarta", "Finalizando")
                    finish()
                }
            }
        }

//        volver.setOnClickListener {
//            val activity = Intent(applicationContext, ActivityCartas::class.java)
//            startActivity(activity)
//        }

        icono.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val accesoGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                urlCarta = uri
                icono.setImageURI(uri)
            }
        }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}
