package com.example.myapplication.Carta


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.Evento.Evento
import com.example.myapplication.Pedido.Pedido
import com.example.myapplication.R
import com.example.myapplication.Registro.MainActivity
import com.example.myapplication.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
class Utilidades {
    companion object {


        var db_ref = FirebaseDatabase.getInstance().reference
        var st = FirebaseStorage.getInstance().reference
        var auth = FirebaseAuth.getInstance()
        fun existeCarta(cartas: List<Carta>, nombre: String): Boolean {
            return cartas.any { it.nombreCarta.lowercase() == nombre.lowercase() }
        }

        fun cogerAdmin(context: Context): String {
            var sp = PreferenceManager.getDefaultSharedPreferences(context)
            var admin = sp.getString("admin", "0")
            return admin!!
        }

        fun registrarCartaEnBaseDatos(
            dbRef: DatabaseReference,
            id: String,
            nombreCarta: String,
            precio: Double,
            stock: Int,
            disponibilidad: String,
            color: String,
            urlCarta: String
        ) {
            val nuevaCarta = Carta(id, nombreCarta, precio, stock, disponibilidad, color, urlCarta)
            dbRef.child("Tienda").child("Cartas").child(id).setValue(nuevaCarta)
            Log.v("Firebase Error", "Error al guardar la carta")
        }

        fun registrarEventoEnBaseDatos(
            dbRef: DatabaseReference,
            id: String,
            nombre: String,
            fecha: String,
            precio: Double,
            aforoMax: Int,
            urlImagen: String
        ) {
            val nuevoEvento = Evento(id, nombre, fecha, precio, 0, aforoMax, urlImagen)
            dbRef.child("Tienda").child("Eventos").child(id).setValue(nuevoEvento)
        }


        suspend fun guardarIcono(stoRef: StorageReference, id: String, imagen: Uri): String {
            val urlCartaFirebase = stoRef.child("cartas").child("mtg").child("imagenes").child(id)
                .putFile(imagen).await().storage.downloadUrl.await().toString()

            return urlCartaFirebase
        }

        suspend fun guardarEvento(stoRef: StorageReference, id: String, imagen: Uri): String {
            val urlCartaFirebase =
                stoRef.child("Tienda").child("eventos").child("imagenes").child(id)
                    .putFile(imagen).await().storage.downloadUrl.await().toString()

            return urlCartaFirebase
        }

        suspend fun cogerUsuario(): Usuario? {
            var user: Usuario? = null
            val datasnapshot =
                db_ref.child("Tienda").child("Usuarios").child(FirebaseAuth.getInstance().uid!!)
                    .get().await()
            user = datasnapshot.getValue(Usuario::class.java)
            return user
        }

        fun obtenerListaCartas(dbRef: DatabaseReference): MutableList<Carta> {
            val lista = mutableListOf<Carta>()

            dbRef.child("Tienda")
                .child("Cartas")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val pojoCarta = childSnapshot.getValue(Carta::class.java)
                            if (pojoCarta != null) {
                                lista.add(pojoCarta)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar errores de lectura de datos
                    }
                })

            return lista
        }

                fun obtenerListaEventos(dbRef: DatabaseReference): MutableList<Evento> {
                    val listaEventos = mutableListOf<Evento>()

                    dbRef.child("Tienda").child("Eventos")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { hijo: DataSnapshot? ->
                                    val pojo_evento = hijo?.getValue(Evento::class.java)
                                    if (pojo_evento != null) {
                                        listaEventos.add(pojo_evento)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                println(error.message)
                            }
                        })

                    return listaEventos
                }

        fun updatePedido(context: Context, pedido: Pedido) {
            val dbRef = FirebaseDatabase.getInstance().reference

            dbRef.child("Pedidos").child(pedido.id!!).setValue(pedido)
                .addOnSuccessListener {
                    Toast.makeText(context, "Pedido updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update Pedido: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fun venderCarta(context: Context, pedido: Pedido) {
            val dbRef = FirebaseDatabase.getInstance().reference

            // Update the Carta status to "no" (not available)
            dbRef.child("Tienda").child("Cartas").child(pedido.idcarta!!).child("disponibilidad").setValue("no")

            // Add the Carta to the user's collection
            dbRef.child("Usuarios").child(pedido.idusuario!!).child("Cartas").child(pedido.idcarta!!).setValue(pedido.nombrecarta)
                .addOnSuccessListener {
                    Toast.makeText(context, "Carta added to user's collection", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to add Carta to user's collection: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


        suspend fun guardarImagenCarta(idGenerado: String, urlimg: Uri): String {
            lateinit var urlimagenfirebase: Uri

            urlimagenfirebase = st.child("Tienda").child("ImagenesEnventos").child(idGenerado)
                .putFile(urlimg).await().storage.downloadUrl.await()

            return urlimagenfirebase.toString()

        }

        fun subirCarta(nuevacarta: Carta) {
            db_ref.child("Tienda").child("Cartas").child(nuevacarta.id!!).setValue(nuevacarta)
        }

        fun subirPedido(pedido: Pedido) {
            db_ref.child("Tienda").child("Pedidos").child(pedido.id!!).setValue(pedido)
        }

        fun subirUsuario(usuario: Usuario) {
            usuario.id?.let { db_ref.child("Tienda").child("Usuarios").child(it).setValue(usuario) }
        }

        fun obtenerListaEventos(): MutableList<Evento> {
            var lista = mutableListOf<Evento>()

            db_ref.child("Tienda").child("Eventos")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { hijo: DataSnapshot ->
                            val pojo_evento = hijo.getValue(Evento::class.java)
                            lista.add(pojo_evento!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }

        fun existeEvento(eventos: List<Evento>, nombre: String): Boolean {
            return eventos.any {
                it.nombre!!.lowercase() == nombre.lowercase()
            }
        }


        suspend fun guardarImagenEvento(idGenerado: String, urlimg: Uri): String {

            lateinit var urlimagenfirebase: Uri

            urlimagenfirebase = st.child("Tienda").child("ImagenesEventos").child(idGenerado)
                .putFile(urlimg).await().storage.downloadUrl.await()

            return urlimagenfirebase.toString()

        }

        fun subirEvento(nuevoevento: Evento) {
            db_ref.child("Tienda").child("Eventos").child(nuevoevento.id!!).setValue(nuevoevento)
        }

        fun venderPedido(contexto: Context, itemActual: Pedido) {
            db_ref.child("Tienda").child("Pedidos").child(itemActual.id!!).child("estado")
                .setValue("1")
            db_ref.child("Tienda").child("Cartas").child(itemActual.idcarta!!).child("disponible")
                .setValue("0")
            Toast.makeText(contexto, "Pedido vendido", Toast.LENGTH_SHORT).show()
        }

        fun denegarPedido(contexto: Context, itemActual: Pedido) {
            //borrar de la base de datos
            db_ref.child("Tienda").child("Pedidos").child(itemActual.id!!).removeValue()
            //poner carta como disponible
            db_ref.child("Tienda").child("Cartas").child(itemActual.idcarta!!).child("disponible")
                .setValue("1")
            Toast.makeText(contexto, "Pedido denegado", Toast.LENGTH_SHORT).show()

        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto: String) {
            activity.runOnUiThread {
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun showPopupMenuOptions(view: View, context: Context) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.menuInflater.inflate(R.menu.opciones, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.cerrarsesion -> {
                        // Aquí va tu código para la opción 1
                        //cierra la sesion
                        val auth = FirebaseAuth.getInstance()
                        auth.signOut()
                        //redirige a la pantalla de login
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        fun animacionCarga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }

        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            return RequestOptions()
                .placeholder(animacionCarga(context))
                .fallback(R.drawable.cartadef)
                .error(R.drawable.error_404)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun obtenerFechaActual(): String {
            val fechaActual = LocalDate.now()
            val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return fechaActual.format(formato)
        }

        suspend fun guardarImagenUser(urlimg: Uri): String {
            lateinit var urlimagenfirebase: Uri

            urlimagenfirebase = st.child("Tienda").child("Usuarios").child(auth.uid.toString())
                .putFile(urlimg).await().storage.downloadUrl.await()

            return urlimagenfirebase.toString()

        }

    }
}



