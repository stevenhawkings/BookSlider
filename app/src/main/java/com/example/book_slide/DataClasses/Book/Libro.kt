package com.example.book_slide.DataClasses.Book

import android.net.Uri

data class Libro(
    val uri: Uri,                  // el archivo (documento) ya copiado/con permiso persistente
    val nombre: String,
    val tipo: String,              // "PDF", "WORD", "EPUB", "TXT", "OTRO"
    val portadaUri: Uri? = null,   // foto de portada opcional
    val id: Long = 0                // opcional: útil si luego quieres eliminar por id en vez de posición
)
