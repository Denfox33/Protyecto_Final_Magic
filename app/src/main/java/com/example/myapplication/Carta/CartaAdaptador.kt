package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Carta.Carta
import com.example.myapplication.Carta.EditarCarta
import com.example.myapplication.Carta.Utilidades
import com.example.myapplication.Pedido.Pedido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.Date

class CartaAdaptador(private var lista_cartas: MutableList<Carta>):
    RecyclerView.Adapter<CartaAdaptador.CartaViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_cartas
    private val isAdmin: Boolean

    init {
        val email = FirebaseAuth.getInstance().currentUser?.email
        isAdmin = email?.endsWith("@admin.com") ?: false
    }

    class CartaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreCarta)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val stock: TextView = itemView.findViewById(R.id.tvStock)
        val disponible: ConstraintLayout = itemView.findViewById(R.id.disponible)
        val color: View = itemView.findViewById(R.id.tvColor)
        val imagen: ImageView = itemView.findViewById(R.id.ivCarta)
        val editar: Button = itemView.findViewById(R.id.btnEditC)
        val eliminar: Button = itemView.findViewById(R.id.btnQuitC)
        val comprar :Button = itemView.findViewById(R.id.comprar)
        val txtdisponible: TextView = itemView.findViewById(R.id.txtdisponible)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartaViewHolder {
        val vista_item =
            LayoutInflater.from(parent.context).inflate(R.layout.item_carta, parent, false)
        contexto = parent.context
        return CartaViewHolder(vista_item)
    }

    override fun getItemCount(): Int = lista_filtrada.size

    override fun onBindViewHolder(holder: CartaViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]

        holder.nombre.text = item_actual.nombreCarta
        holder.precio.text = item_actual.precio.toString()
        holder.stock.text = item_actual.stock.toString()

        if (item_actual.disponibilidad.equals("si", ignoreCase = true)) {
            holder.disponible.setBackgroundColor(Color.GREEN)
            holder.txtdisponible.text = "Disponible"
            holder.comprar.visibility = View.VISIBLE
        } else {
            holder.disponible.setBackgroundColor(Color.RED)
            holder.txtdisponible.text = "No Disponible"
            holder.comprar.visibility = View.GONE
        }

        if (isAdmin) {
            holder.editar.visibility = View.VISIBLE
            holder.eliminar.visibility = View.VISIBLE
            holder.comprar.visibility = View.GONE
        } else {
            holder.editar.visibility = View.GONE
            holder.eliminar.visibility = View.GONE
            holder.comprar.visibility = View.VISIBLE

            holder.comprar.setOnClickListener {
                // Comprueba si la carta está disponible antes de permitir la compra
                if (item_actual.disponibilidad.equals("si", ignoreCase = true)) {
                    // Aquí añades la carta a la colección del usuario en la base de datos
                    val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtén el ID del usuario
                    val dbRef = FirebaseDatabase.getInstance().reference

                    // Create a new order
                    val orderId = dbRef.child("Pedidos").push().key
                    val order = Pedido(
                        id = orderId,
                        cartaId = item_actual.id,
                        userId = userId,
                        cartaNombre = item_actual.nombreCarta,
                        cartaColor = item_actual.color,
                        precio = item_actual.precio,
                        estado = "Pendiente",
                        fecha = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date()),
                        urlCarta = item_actual.urlCarta,
                        notificacionUsuario = Settings.Secure.getString(contexto.contentResolver, Settings.Secure.ANDROID_ID)
                    )

                    orderId?.let {
                        dbRef.child("Pedidos").child(it).setValue(order).addOnSuccessListener {
                            // Reduce the stock of the card in the database
                            val newStock = item_actual.stock - 1
                            dbRef.child("Tienda").child("Cartas").child(item_actual.id).child("stock").setValue(newStock)

                            // Set availability to false if the stock is 0
                            if (newStock == 0) {
                                dbRef.child("Tienda").child("Cartas").child(item_actual.id).child("disponibilidad").setValue("no")
                            }

                            Toast.makeText(contexto, "Pedido creado", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(contexto, "Error al crear el pedido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(contexto, "Esta carta no está disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.nombre.text = item_actual.nombreCarta
        holder.precio.text = item_actual.precio.toString()
        holder.stock.text = item_actual.stock.toString()
        // Crear un mapa de colores a sus representaciones enteras
        val colorMap = mapOf("Azul" to Color.BLUE, "Blanco" to Color.WHITE, "Negro" to Color.BLACK, "Rojo" to Color.RED, "Verde" to Color.GREEN)
        // Buscar el color correspondiente y establecerlo como el color de fondo
        colorMap[item_actual.color]?.let { holder.color.setBackgroundColor(it) }

        Glide.with(contexto).load(item_actual.urlCarta).into(holder.imagen)

        if (item_actual.disponibilidad == "si"){
            holder.disponible.setBackgroundColor(Color.GREEN)

        } else {
            holder.disponible.setBackgroundColor(Color.RED)
        }

        val URL: String? = when (item_actual.urlCarta) {
            "" -> null
            else -> item_actual.urlCarta
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.imagen)

        holder.editar.setOnClickListener {
            val intent = Intent(contexto, EditarCarta::class.java)
            intent.putExtra("carta", item_actual)
            contexto.startActivity(intent)
        }
    }

    fun sortListByOrder(order: String) {
        when (order) {
            "Nombre Ascendente" -> lista_filtrada.sortBy { it.nombreCarta }
            "precio" -> lista_filtrada.sortBy { it.precio }
            "Color" -> lista_filtrada.sortBy { it.color }
            "Stock" -> lista_filtrada.sortBy { it.stock }

        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val busqueda = constraint.toString().toLowerCase()

                lista_filtrada = when (busqueda) {
                    // Filtro por nombre
                    "Nombre Ascendente" -> lista_cartas.sortedBy { it.nombreCarta.toLowerCase() }.toMutableList()
                    "Nombre Descendente" -> lista_cartas.sortedByDescending { it.nombreCarta.toLowerCase() }
                        .toMutableList()

                    // Filtro por color
                    "Color Ascendente" -> lista_cartas.sortedBy { it.color.toLowerCase() }.toMutableList()
                    "Color Descendente" -> lista_cartas.sortedByDescending { it.color.toLowerCase() }
                        .toMutableList()

                    // Filtro por precio
                    "Precio Ascendente" -> lista_cartas.sortedBy { it.precio }.toMutableList()
                    "Precio Descendente" -> lista_cartas.sortedByDescending { it.precio }.toMutableList()

                    // Filtro por stock
                    "Stock Ascendente" -> lista_cartas.sortedBy { it.stock }.toMutableList()
                    "Stock Descendente" -> lista_cartas.sortedByDescending { it.stock }.toMutableList()

                    // Filtro por disponibles
                    "Disponibles Ascendente" -> lista_cartas.sortedBy { it.disponibilidad.toLowerCase() }
                        .toMutableList()
                    "Disponibles Descendente" -> lista_cartas.sortedByDescending { it.disponibilidad.toLowerCase() }
                        .toMutableList()

                    // Sin filtro
                    else -> lista_cartas
                }

                val filterResults = FilterResults()
                filterResults.values = lista_filtrada

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                lista_filtrada = (results?.values as? List<Carta> ?: emptyList()).toMutableList()
                notifyDataSetChanged()
            }
        }
    }
}