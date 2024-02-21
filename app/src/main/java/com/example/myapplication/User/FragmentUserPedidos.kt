package com.example.myapplication.Pedido

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAdminPedidosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentUserPedidos : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentAdminPedidosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PedidoAdaptador
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("FragmentAdminPedidos", "onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminPedidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("Pedidos")

        binding.scEventos.layoutManager = LinearLayoutManager(context)
        adapter = PedidoAdaptador(mutableListOf())
        binding.scEventos.adapter = adapter

        val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtén el ID del usuario

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pedidos = snapshot.children.mapNotNull { it.getValue(Pedido::class.java) }
                val pedidosDelUsuario = pedidos.filter { it.userId == userId } // Filtra los pedidos por el ID del usuario
                adapter.lista_pedidos.clear()
                adapter.lista_pedidos.addAll(pedidosDelUsuario.reversed())
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentAdminPedidos().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}