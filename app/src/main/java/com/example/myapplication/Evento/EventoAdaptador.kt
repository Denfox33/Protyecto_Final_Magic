package com.example.myapplication.Evento

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Carta.Utilidades
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventoAdaptador(private var lista_eventos: MutableList<Evento>) :
    RecyclerView.Adapter<EventoAdaptador.EventoViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_eventos
    private val isAdmin: Boolean

    init {
        val email = FirebaseAuth.getInstance().currentUser?.email
        isAdmin = email?.endsWith("@admin.com") ?: false
    }

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombre)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val precio: TextView = itemView.findViewById(R.id.precio)
        val aforo: TextView = itemView.findViewById(R.id.aforo_actual)
        val aforomax: TextView = itemView.findViewById(R.id.aforo_max)
        val disponible = itemView.findViewById<View>(R.id.disponible)
        val img = itemView.findViewById<ImageView>(R.id.img)
        val unirse = itemView.findViewById<Button>(R.id.adButton)
        val desapuntarse=itemView.findViewById<Button>(R.id.delButton)


        val editar = itemView.findViewById<Button>(R.id.EditarEven)
        val delete =itemView.findViewById<Button>(R.id.EliminarEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): EventoViewHolder {
        val vista_item =
            LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
        contexto = parent.context
        return EventoViewHolder(vista_item)
    }

    override fun getItemCount(): Int = lista_filtrada.size

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]


        // Obtén la cantidad de usuarios unidos al evento
        val dbRef = FirebaseDatabase.getInstance().reference
        item_actual.id?.let {
            dbRef.child("Eventos").child(it).child("UsuariosUnidos")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Actualiza aforo_actual con la cantidad de usuarios unidos al evento
                        val aforoActual = snapshot.childrenCount.toInt()
                        holder.aforo.text = aforoActual.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
        }

        if (isAdmin) {
            holder.delete.visibility = View.VISIBLE
            holder.unirse.visibility = View.GONE
            holder.desapuntarse.visibility = View.GONE
            holder.editar.visibility = View.VISIBLE
        } else {
            holder.delete.visibility = View.GONE
            holder.unirse.visibility = View.VISIBLE
            holder.desapuntarse.visibility = View.VISIBLE
            holder.editar.visibility = View.GONE


            holder.unirse.setOnClickListener {
                val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtén el ID del usuario
                val dbRef = FirebaseDatabase.getInstance().reference
                item_actual.id?.let { it1 ->
                    dbRef.child("Eventos").child(it1).child("UsuariosUnidos").child(userId!!)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists() && item_actual.aforoactual <= item_actual.aforomax!!) {
                                    // El usuario no está unido al evento y hay espacio disponible, así que se une al evento
                                    dbRef.child("Eventos").child(item_actual.id!!).child("UsuariosUnidos").child(userId).setValue(true)
                                    item_actual.aforoactual+=1
                                    dbRef.child("Eventos").child(item_actual.id!!).child("aforoactual").setValue(item_actual.aforoactual)
                                    Toast.makeText(contexto, "Te has unido al evento", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(contexto, "No puedes unirte a este evento", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                println(error.message)
                            }
                        })
                }
            }

            holder.desapuntarse.setOnClickListener {
                val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtén el ID del usuario
                val dbRef = FirebaseDatabase.getInstance().reference
                item_actual.id?.let { it1 ->
                    dbRef.child("Eventos").child(it1).child("UsuariosUnidos").child(userId!!)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // El usuario está unido al evento, así que se desapunta del evento
                                    dbRef.child("Eventos").child(item_actual.id!!).child("UsuariosUnidos").child(userId).removeValue()
                                    item_actual.aforoactual--
                                    dbRef.child("Eventos").child(item_actual.id!!).child("aforoactual").setValue(item_actual.aforoactual)
                                    Toast.makeText(contexto, "Te has desapuntado del evento", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(contexto, "No estás unido a este evento", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                println(error.message)
                            }
                        })
                }
            }

        }

        holder.nombre.text = item_actual.nombre
        holder.fecha.text = item_actual.fecha
        holder.precio.text = item_actual.precio.toString()
        holder.aforo.text = item_actual.aforoactual.toString()
        holder.aforomax.text = item_actual.aforomax.toString()

        if (item_actual.aforoactual == item_actual.aforomax) {
            holder.disponible.setBackgroundColor(Color.RED)
        } else {
            holder.disponible.setBackgroundColor(Color.GREEN)
        }

        val URL: String? = when (item_actual.imagen) {
            "" -> null
            else -> item_actual.imagen
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.img)

        holder.editar.setOnClickListener {
            val fragment = EditarEventoFragment()
            val bundle = Bundle()
            bundle.putString("id", lista_filtrada[position].id)
            bundle.putString("nombre", lista_filtrada[position].nombre)
            bundle.putString("fecha", lista_filtrada[position].fecha)
            bundle.putDouble("precio", lista_filtrada[position].precio!!)
            bundle.putInt("aforoMax", lista_filtrada[position].aforomax!!)
            bundle.putString("imagen", lista_filtrada[position].imagen)
            fragment.arguments = bundle
            val transaction = (contexto as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        holder.delete.setOnClickListener {
            AlertDialog.Builder(contexto).apply {
                setTitle("Eliminar evento")
                setMessage("¿Estás seguro de que quieres eliminar este evento?")
                setPositiveButton("Sí") { _, _ ->
                    val evento = lista_filtrada[position]
                    FirebaseDatabase.getInstance().getReference("Tienda")
                        .child("Eventos")
                        .child(evento.id!!)
                        .removeValue()
                    lista_filtrada.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(contexto, "Evento eliminado", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("No", null)
            }.create().show()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()) {
                    lista_filtrada = lista_eventos
                } else {
                    lista_filtrada = (lista_eventos.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Evento>
                }

                val filterResults = FilterResults()
                filterResults.values = lista_filtrada

                return filterResults

            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }
}