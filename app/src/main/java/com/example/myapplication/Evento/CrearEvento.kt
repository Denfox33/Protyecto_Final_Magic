package com.example.myapplication.Evento

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.example.myapplication.Carta.Utilidades
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

class CrearEvento : AppCompatActivity(), CoroutineScope {

    private lateinit var nombreEvento: EditText
    private lateinit var fecha: EditText
    private lateinit var precio: EditText
    private lateinit var aforoMax: EditText
    private lateinit var imagen: ImageView
    private lateinit var crear: AppCompatButton
    private lateinit var volver: AppCompatButton

    private var urlEvento: Uri? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var stoRef: StorageReference
    private lateinit var listaEventos: MutableList<Evento>

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_evento)

        job = Job()

        nombreEvento = findViewById(R.id.etNombre)
        fecha = findViewById(R.id.etFecha)
        precio = findViewById(R.id.etPrecio)
        aforoMax = findViewById(R.id.etAforo)
        imagen = findViewById(R.id.img)
        crear = findViewById(R.id.btnCrear)



        dbRef = FirebaseDatabase.getInstance().reference
        stoRef = FirebaseStorage.getInstance().reference
        listaEventos = Utilidades.obtenerListaEventos(dbRef)

        crear.setOnClickListener {
            val nombreEventoTexto = nombreEvento.text.toString().trim()
            val fechaTexto = fecha.text.toString().trim()
            val precioTexto = precio.text.toString().trim()
            val aforoMaxTexto = aforoMax.text.toString().trim()

            if (nombreEventoTexto.isEmpty() || fechaTexto.isEmpty() || precioTexto.isEmpty() || aforoMaxTexto.isEmpty()) {
                Toast.makeText(applicationContext, "Faltan datos en el formulario", Toast.LENGTH_SHORT).show()
            } else if (urlEvento == null) {
                Toast.makeText(applicationContext, "Falta seleccionar la imagen", Toast.LENGTH_SHORT).show()
            } else if (Utilidades.existeEvento(listaEventos, nombreEventoTexto)) {
                Toast.makeText(applicationContext, "Ese evento ya existe", Toast.LENGTH_SHORT).show()
            } else {
                var idGenerado: String? = dbRef.child("Tienda").child("Eventos").push().key

                launch {
                    val urlEventoFirebase = Utilidades.guardarEvento(stoRef, idGenerado!!, urlEvento!!)

                    Utilidades.registrarEventoEnBaseDatos(
                        dbRef, idGenerado!!,
                        nombreEventoTexto,
                        fechaTexto,
                        precioTexto.toDouble(),
                        aforoMaxTexto.toInt(),
                        urlEventoFirebase
                    )

                    Utilidades.tostadaCorrutina(this@CrearEvento, applicationContext, "Evento creado con éxito")
                    finish() // Cierra la actividad actual y vuelve a la actividad anterior
                }
            }
        }



        imagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
        volver.setOnClickListener {
            finish()
        }
    }


    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            urlEvento = uri
            imagen.setImageURI(uri)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}