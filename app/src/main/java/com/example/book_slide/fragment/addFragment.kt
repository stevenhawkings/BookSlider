package com.example.book_slide.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.book_slide.DataClasses.Book.Libro
import com.example.book_slide.DataClasses.Book.LibrosViewModel
import com.example.book_slide.R
import com.example.book_slide.util.ColorBlindnessManager
import com.example.book_slide.util.ColorBlindnessMode
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class addFragment : Fragment() {

    private val librosViewModel: LibrosViewModel by activityViewModels()

    private var uriSeleccionado: Uri? = null
    private var nombreDetectado: String? = null
    private var tipoSeleccionado: String? = null
    private var portadaSeleccionada: Uri? = null

    private lateinit var etNombre: TextInputEditText
    private lateinit var ivPortada: ImageView
    private lateinit var btnGuardar: MaterialButton
    private lateinit var tvTituloAdd: TextView
    private lateinit var btnSeleccionar: MaterialButton
    private lateinit var btnSeleccionarPortada: MaterialButton

    private val mimeTypesPermitidos = arrayOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
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

        tvTituloAdd = view.findViewById(R.id.tvTituloAdd)
        etNombre = view.findViewById(R.id.etNombre)
        ivPortada = view.findViewById(R.id.ivPortada)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnSeleccionar = view.findViewById(R.id.btnSeleccionar)
        btnSeleccionarPortada = view.findViewById(R.id.btnSeleccionarPortada)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ColorBlindnessManager.currentMode.collect { mode ->
                    applyColorBlindnessMode(mode)
                }
            }
        }

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

    private fun applyColorBlindnessMode(mode: ColorBlindnessMode) {
        val colorStateList = ColorStateList.valueOf(mode.primaryColorHex)
        tvTituloAdd.setTextColor(mode.primaryColorHex)
        btnSeleccionar.backgroundTintList = colorStateList
        btnSeleccionar.setTextColor(0xFF000000.toInt())
        btnSeleccionarPortada.setTextColor(mode.primaryColorHex)
        btnSeleccionarPortada.strokeColor = colorStateList
    }

    private fun procesarArchivoSeleccionado(uri: Uri) {
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

        etNombre.setText(nombre)
        btnGuardar.isEnabled = true
        btnGuardar.backgroundTintList = ColorStateList.valueOf(ColorBlindnessManager.getMode(requireContext()).primaryColorHex)
        btnGuardar.setTextColor(0xFF000000.toInt())
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
        val nombreFinal = etNombre.text?.toString()?.trim()
            .let { if (it.isNullOrBlank()) (nombreDetectado ?: "Archivo sin nombre") else it }

        librosViewModel.agregar(Libro(uri, nombreFinal, tipo, portadaSeleccionada))

        uriSeleccionado = null
        nombreDetectado = null
        tipoSeleccionado = null
        portadaSeleccionada = null
        etNombre.text?.clear()
        ivPortada.setImageResource(android.R.drawable.ic_menu_report_image)
        btnGuardar.isEnabled = false

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()
    }
}
