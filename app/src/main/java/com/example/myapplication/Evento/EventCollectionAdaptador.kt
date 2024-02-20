package com.example.myapplication.Evento

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class EventCollectionAdaptador(private var lista_eventos: MutableList<Evento>):
    RecyclerView.Adapter<EventCollectionAdaptador.EventoViewHolder>() {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_eventos

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.nombre)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val aforo: TextView = itemView.findViewById(R.id.aforo_actual)
        val aforo_max: TextView = itemView.findViewById(R.id.aforo_max)
        val imagen: ImageView = itemView.findViewById(R.id.img)
        val apuntarse: Button = itemView.findViewById(R.id.adButton)
        val salir: Button = itemView.findViewById(R.id.delButton)
        val editar: Button = itemView.findViewById(R.id.EditarEven)
        val eliminar: Button = itemView.findViewById(R.id.delButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val vista_item =
            LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
        contexto = parent.context
        return EventoViewHolder(vista_item)
    }

    override fun getItemCount(): Int = lista_filtrada.size

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]

        // Hide the buttons
        holder.editar.visibility = View.GONE
        holder.eliminar.visibility = View.GONE
        holder.eliminar.visibility = View.GONE
        holder.salir.visibility = View.VISIBLE
        // Other code remains the same...
    }
}