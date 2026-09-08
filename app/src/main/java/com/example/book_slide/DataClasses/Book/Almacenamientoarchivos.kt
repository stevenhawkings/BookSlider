package com.example.book_slide.DataClasses.Book

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

/**
 * Copia el archivo apuntado por [uriOrigen] (que viene del selector del sistema, con permiso
 * de lectura temporal) hacia una carpeta privada de la app: filesDir/libros/.
 *
 * Esto es lo que garantiza que el archivo "se guarde en el dispositivo y no se elimine al
 * salir de la app": a partir de aquí ya no depende del archivo original (puede moverse o
 * borrarse de Descargas y esta copia sigue intacta). Solo se pierde si se desinstala la app
 * o se borran sus datos.
 */
object AlmacenamientoArchivos {

    fun copiarAlmacenamientoInterno(context: Context, uriOrigen: Uri, nombreOriginal: String): Uri {
        val carpetaDestino = File(context.filesDir, "libros").apply { mkdirs() }
        val nombreUnico = "${System.currentTimeMillis()}_$nombreOriginal"
        val archivoDestino = File(carpetaDestino, nombreUnico)

        context.contentResolver.openInputStream(uriOrigen)?.use { entrada ->
            archivoDestino.outputStream().use { salida -> entrada.copyTo(salida) }
        }

        return Uri.fromFile(archivoDestino)
    }

    fun obtenerNombreOriginal(context: Context, uri: Uri): String? {
        var nombre: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx != -1) nombre = cursor.getString(idx)
            }
        }
        return nombre
    }

    fun detectarTipo(nombre: String): String = when {
        nombre.endsWith(".pdf", true) -> "PDF"
        nombre.endsWith(".doc", true) || nombre.endsWith(".docx", true) -> "WORD"
        nombre.endsWith(".epub", true) -> "EPUB"
        nombre.endsWith(".txt", true) -> "TXT"
        else -> "OTRO"
    }

    /** Borra el archivo físico de filesDir/libros cuando se elimina el item de la lista. */
    fun eliminarArchivoFisico(uri: Uri) {
        uri.path?.let { ruta -> File(ruta).takeIf { it.exists() }?.delete() }
    }
}