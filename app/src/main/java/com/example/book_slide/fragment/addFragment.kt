package com.example.book_slide.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.book_slide.Book.Libro
import com.example.book_slide.Book.LibrosViewModel
import com.example.book_slide.R
import com.google.android.material.button.MaterialButton

class addFragment : Fragment() {

    // Mismo ViewModel que usa HomeFragment: al guardar aquí, aparece allá.
    private val librosViewModel: LibrosViewModel by activityViewModels()

    private var uriSeleccionado: Uri? = null
    private var nombreSeleccionado: String? = null
    private var tipoSeleccionado: String? = null

    private lateinit var tvArchivoSeleccionado: TextView
    private lateinit var btnGuardar: MaterialButton

    // Tipos de archivo permitidos. Agrega más MIME types aquí si quieres aceptar otros formatos.
    private val mimeTypesPermitidos = arrayOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/epub+zip",
        "text/plain"
    )

    private val selectorArchivo = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { procesarArchivoSeleccionado(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvArchivoSeleccionado = view.findViewById(R.id.tvArchivoSeleccionado)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        val btnSeleccionar = view.findViewById<MaterialButton>(R.id.btnSeleccionar)

        btnSeleccionar.setOnClickListener {
            selectorArchivo.launch(mimeTypesPermitidos)
        }

        btnGuardar.setOnClickListener {
            guardarLibro()
        }
    }

    private fun procesarArchivoSeleccionado(uri: Uri) {
        // Permiso persistente para poder volver a abrir el archivo después
        requireContext().contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val nombre = obtenerNombreArchivo(uri) ?: "Archivo sin nombre"
        val tipo = when {
            nombre.endsWith(".pdf", true) -> "PDF"
            nombre.endsWith(".doc", true) || nombre.endsWith(".docx", true) -> "WORD"
            nombre.endsWith(".epub", true) -> "EPUB"
            nombre.endsWith(".txt", true) -> "TXT"
            else -> "OTRO"
        }

        uriSeleccionado = uri
        nombreSeleccionado = nombre
        tipoSeleccionado = tipo

        tvArchivoSeleccionado.text = nombre
        btnGuardar.isEnabled = true
    }

    private fun obtenerNombreArchivo(uri: Uri): String? {
        var nombre: String? = null
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx != -1) nombre = it.getString(idx)
            }
        }
        return nombre
    }

    private fun guardarLibro() {
        val uri = uriSeleccionado ?: return
        val nombre = nombreSeleccionado ?: return
        val tipo = tipoSeleccionado ?: "OTRO"

        librosViewModel.agregar(Libro(uri, nombre, tipo))

        // Limpiar selección
        uriSeleccionado = null
        nombreSeleccionado = null
        tipoSeleccionado = null
        tvArchivoSeleccionado.text = "Ningún archivo seleccionado"
        btnGuardar.isEnabled = false

        // Volver al Home para ver el archivo recién agregado
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()
    }
}