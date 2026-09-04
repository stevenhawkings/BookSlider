package com.example.book_slide.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.book_slide.Book.Libro
import com.example.book_slide.R

class LibroAdapter(
    private var items: MutableList<Libro>,
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcono: ImageView = view.findViewById(R.id.ivIcono)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    /** Reemplaza la lista completa (se llama cada vez que el ViewModel notifica un cambio). */
    fun actualizarLista(nuevaLista: List<Libro>) {
        items = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = items[position]
        holder.tvNombre.text = libro.nombre
        holder.tvTipo.text = libro.tipo

        val icono = when (libro.tipo) {
            "PDF" -> android.R.drawable.ic_menu_agenda
            "WORD" -> android.R.drawable.ic_menu_edit
            "EPUB" -> android.R.drawable.ic_menu_slideshow
            "TXT" -> android.R.drawable.ic_menu_view
            else -> android.R.drawable.ic_menu_help
        }
        holder.ivIcono.setImageResource(icono)

        holder.btnEliminar.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onEliminar(pos)
        }
    }

    override fun getItemCount(): Int = items.size
}