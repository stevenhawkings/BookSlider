package com.example.book_slide.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.book_slide.DataClasses.Book.Libro


/**
 * Tarjeta plegable de un archivo.
 * - Punto 1 (tarjeta plegable): estado local `expandido` togglea el contenido extra.
 * - Punto 3 (animateDpAsState): la altura del contenido extra se anima al expandir/colapsar.
 * - Punto 6 (accesibilidad): `semantics` describe cada acción para TalkBack.
 *
 * Interacción: tocar el nombre/ícono ABRE el archivo con la app del sistema.
 * Tocar la flechita expande/colapsa el detalle. Son gestos separados a propósito,
 * para que no compitan entre sí sobre la misma tarjeta.
 */
@Composable
fun EventCard(libro: Libro, modifier: Modifier = Modifier) {
    var expandido by rememberSaveable { mutableStateOf(false) }
    val contexto = LocalContext.current

    val alturaContenidoExtra by animateDpAsState(
        targetValue = if (expandido) 48.dp else 0.dp,
        label = "alturaContenidoExtra"
    )
    val rotacionFlecha by animateFloatAsState(
        targetValue = if (expandido) 180f else 0f,
        label = "rotacionFlecha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { abrirArchivo(contexto, libro.uri, libro.tipo) }
                    .semantics {
                        contentDescription = "Archivo ${libro.nombre}, tipo ${libro.tipo}"
                        onClick(label = "Abrir archivo") { true }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null, // decorativo: la Row ya describe la acción completa
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = libro.nombre,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { expandido = !expandido },
                    modifier = Modifier.semantics {
                        contentDescription = if (expandido) "Colapsar detalles" else "Expandir detalles"
                        stateDescription = if (expandido) "Expandido" else "Colapsado"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotacionFlecha)
                    )
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .height(alturaContenidoExtra)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (alturaContenidoExtra > 0.dp) {
                    Text(
                        text = "Tipo: ${libro.tipo}",
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}

/** Abre el archivo con la app del sistema correspondiente (lector de PDF, Word, etc.). */
private fun abrirArchivo(contexto: Context, uri: Uri, tipo: String) {
    // El archivo vive en filesDir/libros como file://, pero Android prohíbe pasar
    // ese esquema a otra app (FileUriExposedException). Lo convertimos a un
    // content:// temporal y seguro a través de FileProvider.
    val uriParaAbrir = if (uri.scheme == "file") {
        FileProvider.getUriForFile(
            contexto,
            "${contexto.packageName}.fileprovider",
            File(uri.path!!)
        )
    } else {
        uri
    }

    val mimeType = contexto.contentResolver.getType(uriParaAbrir) ?: when (tipo) {
        "PDF" -> "application/pdf"
        "WORD" -> "application/msword"
        "EPUB" -> "application/epub+zip"
        "TXT" -> "text/plain"
        else -> "*/*"
    }

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uriParaAbrir, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        contexto.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            contexto,
            "No hay ninguna app instalada que pueda abrir este archivo",
            Toast.LENGTH_SHORT
        ).show()
    }
}