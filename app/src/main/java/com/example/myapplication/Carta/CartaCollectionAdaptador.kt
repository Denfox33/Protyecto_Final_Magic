package com.example.myapplication.Carta

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class CartaCollectionAdaptador(private var lista_cartas: MutableList<Carta>):
    RecyclerView.Adapter<CartaCollectionAdaptador.CartaViewHolder>() {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_cartas


    class CartaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreCarta)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val stock: TextView = itemView.findViewById(R.id.tvStock)
        val disponible: ConstraintLayout = itemView.findViewById(R.id.disponible)
        val color: View = itemView.findViewById(R.id.tvColor)
        val imagen: ImageView = itemView.findViewById(R.id.ivCarta)
        val editar: Button = itemView.findViewById(R.id.btnEditC)
        val eliminar: Button = itemView.findViewById(R.id.btnQuitC)
        val comprar: Button = itemView.findViewById(R.id.comprar)
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

        // Hide the buttons
        holder.editar.visibility = View.GONE
        holder.eliminar.visibility = View.GONE
        holder.comprar.visibility = View.GONE

        // Other code remains the same...
    }
}