package com.example.myapplication.Carta


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditarCarta : AppCompatActivity(), CoroutineScope {

    private lateinit var nombreCarta: EditText
    private lateinit var precio: EditText
    private lateinit var stock: EditText
    private lateinit var disponibilidad: AutoCompleteTextView
    private lateinit var color: AutoCompleteTextView
    private lateinit var icono: ImageView
    private lateinit var modificar: Button
    private lateinit var volver: Button

    private var urlCarta: Uri? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var stoRef: StorageReference
    private lateinit var pojoCarta: Carta
    private lateinit var listaCartas: MutableList<Carta>
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carta_modificar)

        color = findViewById(R.id.tetColor)
        disponibilidad = findViewById(R.id.tetDisponible)

        // Crea un ArrayAdapter utilizando el array de colores y un layout simple de spinner
        val adapterColor = ArrayAdapter.createFromResource(
            this,
            R.array.color_cartas,
            android.R.layout.simple_spinner_dropdown_item
        )

        // Configura el AutoCompleteTextView para usar el ArrayAdapter
        color.setAdapter(adapterColor)

        // Crea un ArrayAdapter utilizando el array de disponibilidad y un layout simple de spinner
        val adapterDisponible = ArrayAdapter.createFromResource(
            this,
            R.array.disponibilidad_cartas,
            android.R.layout.simple_spinner_dropdown_item
        )

        // Configura el AutoCompleteTextView para usar el ArrayAdapter
        disponibilidad.setAdapter(adapterDisponible)



        val thisActivity = this
        job = Job()

        pojoCarta = intent.getParcelableExtra("carta") ?: Carta()

        nombreCarta = findViewById(R.id.tietNombre)
        precio = findViewById(R.id.tietPrecio)
        stock = findViewById(R.id.tietStock)
        disponibilidad = findViewById(R.id.tetDisponible)
        color = findViewById(R.id.tetColor)
        icono = findViewById(R.id.ivImagen)
        modificar = findViewById(R.id.btnEditarCarta)
        volver = findViewById(R.id.Volver)

        nombreCarta.setText(pojoCarta.nombreCarta)
        precio.setText(pojoCarta.precio.toString())
        stock.setText(pojoCarta.stock.toString())
        disponibilidad.setText(pojoCarta.disponibilidad)
        color.setText(pojoCarta.color)

        Glide.with(applicationContext)
            .load(pojoCarta.urlCarta)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(icono)

        dbRef = FirebaseDatabase.getInstance().getReference()
        stoRef = FirebaseStorage.getInstance().getReference()

        listaCartas = Utilidades.obtenerListaCartas(dbRef)

        modificar.setOnClickListener {
            if (nombreCarta.text.toString().trim().isEmpty() ||
                precio.text.toString().trim().isEmpty() ||
                stock.text.toString().trim().isEmpty() ||
                disponibilidad.text.toString().trim().isEmpty() ||
                color.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()
            } else {

                var urlCartaFirebase: String = pojoCarta.urlCarta!!

                launch {
                    if (urlCarta != null) {
                        urlCartaFirebase =
                            Utilidades.guardarIcono(stoRef, pojoCarta.id!!, urlCarta!!)
                    }
                    modificar.setOnClickListener {
                        if (nombreCarta.text.toString().trim().isEmpty() ||
                            precio.text.toString().trim().isEmpty() ||
                            stock.text.toString().trim().isEmpty() ||
                            disponibilidad.text.toString().trim().isEmpty() ||
                            color.text.toString().trim().isEmpty()
                        ) {
                            Toast.makeText(
                                applicationContext, "Faltan datos en el " +
                                        "formulario", Toast.LENGTH_SHORT
                            ).show()
                        } else {

                            var urlCartaFirebase: String = pojoCarta.urlCarta!!

                            launch {
                                if (urlCarta != null) {
                                    urlCartaFirebase =
                                        Utilidades.guardarIcono(stoRef, pojoCarta.id!!, urlCarta!!)
                                }


                                Utilidades.registrarCartaEnBaseDatos(
                                    dbRef, pojoCarta.id!!,
                                    nombreCarta.text.toString().trim(),
                                    precio.text.toString().trim().toDouble(),
                                    stock.text.toString().trim().toInt(),
                                    disponibilidad.text.toString().trim(),
                                    color.text.toString().trim(),
                                    urlCartaFirebase
                                )

                                Utilidades.tostadaCorrutina(
                                    thisActivity,
                                    applicationContext,
                                    "Carta modificada con éxito"
                                )

                                finish()
                            }
                        }
                    }

                    Utilidades.registrarCartaEnBaseDatos(
                        dbRef, pojoCarta.id!!,
                        nombreCarta.text.toString().trim(),
                        precio.text.toString().trim().toDouble(),
                        stock.text.toString().trim().toInt(),
                        disponibilidad.text.toString().trim(),
                        color.text.toString().trim(),
                        urlCartaFirebase
                    )

                    Utilidades.tostadaCorrutina(
                        thisActivity,
                        applicationContext,
                        "Carta modificada con éxito"
                    )

                    finish()
                }
            }
        }

        volver.setOnClickListener {
          finish()
        }

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
