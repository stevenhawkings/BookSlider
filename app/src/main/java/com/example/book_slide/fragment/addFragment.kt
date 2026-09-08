package com.example.book_slide.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.book_slide.DataClasses.Book.Libro
import com.example.book_slide.DataClasses.Book.LibrosViewModel
import com.example.book_slide.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class addFragment : Fragment() {

    // Mismo ViewModel que usa HomeFragment: al guardar aquí, aparece allá.
    private val librosViewModel: LibrosViewModel by activityViewModels()

    private var uriSeleccionado: Uri? = null
    private var nombreDetectado: String? = null
    private var tipoSeleccionado: String? = null
    private var portadaSeleccionada: Uri? = null

    private lateinit var etNombre: TextInputEditText
    private lateinit var ivPortada: ImageView
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

    private val selectorPortada = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { procesarPortadaSeleccionada(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNombre = view.findViewById(R.id.etNombre)
        ivPortada = view.findViewById(R.id.ivPortada)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        val btnSeleccionar = view.findViewById<MaterialButton>(R.id.btnSeleccionar)
        val btnSeleccionarPortada = view.findViewById<MaterialButton>(R.id.btnSeleccionarPortada)

        btnSeleccionar.setOnClickListener {
            selectorArchivo.launch(mimeTypesPermitidos)
        }

        btnSeleccionarPortada.setOnClickListener {
            selectorPortada.launch(arrayOf("image/*"))
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
        nombreDetectado = nombre
        tipoSeleccionado = tipo

        // Precarga el nombre detectado en el campo editable; el usuario puede cambiarlo antes de guardar.
        etNombre.setText(nombre)
        btnGuardar.isEnabled = true
    }

    private fun procesarPortadaSeleccionada(uri: Uri) {
        requireContext().contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        portadaSeleccionada = uri
        ivPortada.setImageURI(uri)
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
        val tipo = tipoSeleccionado ?: "OTRO"
        // Usa el nombre que el usuario haya dejado en el campo (editado o no).
        val nombreFinal = etNombre.text?.toString()?.trim()
            .let { if (it.isNullOrBlank()) (nombreDetectado ?: "Archivo sin nombre") else it }

        librosViewModel.agregar(Libro(uri, nombreFinal, tipo, portadaSeleccionada))

        // Limpiar selección
        uriSeleccionado = null
        nombreDetectado = null
        tipoSeleccionado = null
        portadaSeleccionada = null
        etNombre.text?.clear()
        ivPortada.setImageResource(android.R.drawable.ic_menu_report_image)
        btnGuardar.isEnabled = false

        // Volver al Home para ver el archivo recién agregado
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()
    }
}
