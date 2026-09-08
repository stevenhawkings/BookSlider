package com.example.book_slide.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.book_slide.DataClasses.Book.AlmacenamientoArchivos
import com.example.book_slide.viewmodel.LibrosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Tipos de archivo permitidos. Agrega más MIME types aquí si quieres aceptar otros formatos.
private val mimeTypesPermitidos = arrayOf(
    "application/pdf",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
    "application/epub+zip",
    "text/plain"
)

@Composable
fun FormularioLibroScreen(
    viewModel: LibrosViewModel = hiltViewModel(),
    onGuardado: () -> Unit
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    var uriLocal by remember { mutableStateOf<Uri?>(null) }
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val selectorArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uriOrigen ->
        uriOrigen?.let { uri ->
            cargando = true
            scope.launch {
                val nombreOriginal = withContext(Dispatchers.IO) {
                    AlmacenamientoArchivos.obtenerNombreOriginal(contexto, uri) ?: "archivo"
                }
                val copiado = withContext(Dispatchers.IO) {
                    AlmacenamientoArchivos.copiarAlmacenamientoInterno(contexto, uri, nombreOriginal)
                }
                uriLocal = copiado
                nombre = nombreOriginal
                tipo = AlmacenamientoArchivos.detectarTipo(nombreOriginal)
                cargando = false
            }
        }
    }

    // Punto 5: validación reactiva — se re-ejecuta cada vez que cambian nombre o uriLocal.
    LaunchedEffect(nombre, uriLocal) {
        error = when {
            uriLocal == null -> "Selecciona un archivo"
            nombre.isBlank() -> "El archivo no tiene un nombre válido"
            else -> null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Agregar archivo", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        Text(
            text = if (cargando) "Copiando archivo..." else nombre.ifBlank { "Ningún archivo seleccionado" },
            modifier = Modifier.semantics {
                contentDescription = "Archivo seleccionado: ${nombre.ifBlank { "ninguno" }}"
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { selectorArchivo.launch(mimeTypesPermitidos) },
            modifier = Modifier.semantics { contentDescription = "Seleccionar archivo del dispositivo" }
        ) {
            Text("Seleccionar archivo")
        }

        error?.let { mensaje ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                // liveRegion: TalkBack anuncia el error automáticamente en cuanto aparece
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val uri = uriLocal ?: return@Button
                viewModel.agregar(uri, nombre, tipo)
                onGuardado()
            },
            enabled = error == null && !cargando,
            modifier = Modifier.semantics { contentDescription = "Guardar archivo agregado" }
        ) {
            Text("Guardar")
        }
    }
}