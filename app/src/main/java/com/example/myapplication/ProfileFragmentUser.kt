package com.example.myapplication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Evento.Evento
import com.example.myapplication.Evento.EventoAdaptador
import com.example.myapplication.databinding.FragmentProfileUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragmentUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragmentUser : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    class MainFragmentUser : Fragment() {
//        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: EventoAdaptador
        private var listaEventos = mutableListOf<Evento>()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_main_user, container, false)

            // Inicializa el RecyclerView
//            recyclerView = view.findViewById(R.id.recyclerView)

            // Crea una instancia del adaptador y pásale la lista de eventos
            adapter = EventoAdaptador(listaEventos)

            // Establece el adaptador y el LayoutManager para el RecyclerView
//            recyclerView.adapter = adapter
//            recyclerView.layoutManager = LinearLayoutManager(context)

            // Llena la lista con datos y notifica al adaptador que los datos han cambiado
            obtenerDatos()

            return view
        }

        private fun obtenerDatos() {
            // Aquí obtienes los datos para tu lista y notificas al adaptador que los datos han cambiado
            val dbRef = FirebaseDatabase.getInstance().reference
            dbRef.child("Tienda").child("Eventos")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listaEventos.clear()
                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val pojo_evento = hijo?.getValue(Evento::class.java)
                            if (pojo_evento != null) {
                                listaEventos.add(pojo_evento)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragmentUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}