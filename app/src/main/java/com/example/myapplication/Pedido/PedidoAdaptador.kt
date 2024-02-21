package com.example.myapplication.Pedido

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Carta.Utilidades
import com.example.myapplication.R

class PedidoAdaptador(var lista_pedidos: MutableList<Pedido>) :
    RecyclerView.Adapter<PedidoAdaptador.PedidoViewHolder>() {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_pedidos

    fun filter(text: String) {
        val filteredList = lista_pedidos.filter { pedido ->
            pedido.cartaNombre?.contains(text, ignoreCase = true) == true
        }
        lista_pedidos = filteredList.toMutableList()
        notifyDataSetChanged()
    }
    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id_pedido: TextView = itemView.findViewById(R.id.id_pedido)
        val id_carta: TextView = itemView.findViewById(R.id.id_carta)
        val id_cliente: TextView = itemView.findViewById(R.id.id_cliente)
        val precio: TextView = itemView.findViewById(R.id.precio)
        val nombre_carta: TextView = itemView.findViewById(R.id.nombre_carta)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val aceptar: Button = itemView.findViewById(R.id.aceptar)
        val denegar: Button = itemView.findViewById(R.id.denegar)
        val desplegable: View = itemView.findViewById(R.id.desplegable)
        val btndesplegar: ImageView = itemView.findViewById(R.id.boton_desplegable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val vista_item =
            LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        contexto = parent.context
        return PedidoViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]
        holder.id_pedido.text = item_actual.id
        holder.id_carta.text = item_actual.cartaId
        holder.id_cliente.text = item_actual.userId
        holder.precio.text = item_actual.precio.toString()
        holder.nombre_carta.text = item_actual.cartaNombre
        holder.fecha.text = item_actual.fecha


        holder.btndesplegar.setOnClickListener {
            if (holder.desplegable.visibility == View.VISIBLE) {
                holder.desplegable.visibility = View.GONE
                holder.btndesplegar.setImageResource(R.drawable.opem)
            } else {
                holder.desplegable.visibility = View.VISIBLE
                holder.btndesplegar.setImageResource(R.drawable.close)
            }
        }

        holder.aceptar.setOnClickListener {
            AlertDialog.Builder(contexto)
                .setTitle("Vender pedido")
                .setMessage("¿Estás seguro de que quieres vender este pedido?. La carta se pondra como no disponible.")
                .setPositiveButton("Sí") { dialog, which ->
                    // Update the Pedido status to 1 (accepted)
                    item_actual.estado ="Aceptado"
                    Utilidades.updatePedido(contexto, item_actual)

                    // Add the card to the user's collection and update its availability
                    Utilidades.venderCarta(contexto, item_actual)

                    holder.desplegable.visibility = View.GONE
                    holder.btndesplegar.setImageResource(R.drawable.opem)
                }
                .setNegativeButton("No") { dialog, which -> }
                .show()
        }

        holder.denegar.setOnClickListener {
            AlertDialog.Builder(contexto)
                .setTitle("Denegar pedido")
                .setMessage("¿Estás seguro de que quieres denegar este pedido?. La carta se pondra como disponible para todos los usuarios.")
                .setPositiveButton("Sí") { dialog, which ->
                    // Update the Pedido status to 2 (denied)
                    item_actual.estado = "Denegado"
                    Utilidades.updatePedido(contexto, item_actual)

                    holder.desplegable.visibility = View.GONE
                    holder.btndesplegar.setImageResource(R.drawable.opem)
                }
                .setNegativeButton("No") { dialog, which -> }
                .show()
        }

    }


}