package com.example.book_slide.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.book_slide.Book.LibrosViewModel
import com.example.book_slide.R
import com.example.book_slide.adapter.LibroAdapter
import com.example.book_slide.ui.DocumentationSearchBar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // Se comparte con addFragment: lo que se agrega ahí aparece aquí.
    private val librosViewModel: LibrosViewModel by activityViewModels()
    private lateinit var adapter: LibroAdapter
    private lateinit var tvVacio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Llamar barra de busqueda
        val searchCompose = view.findViewById<ComposeView>(R.id.searchCompose)

        searchCompose.setContent {
            DocumentationSearchBar()
        }

        // Se agrega la vista de las tarjetas.
        tvVacio = view.findViewById(R.id.tvVacio)
        val rvLibros = view.findViewById<RecyclerView>(R.id.rvLibros)

        adapter = LibroAdapter(mutableListOf()) { position ->
            librosViewModel.eliminar(position)
        }
        rvLibros.layoutManager = LinearLayoutManager(requireContext())
        rvLibros.adapter = adapter

        librosViewModel.libros.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
            tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_home2,
            container,
            false
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

