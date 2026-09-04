package com.example.book_slide.Book

import android.net.Uri

data class Libro(
    val uri: Uri,
    val nombre: String,
    val tipo: String // "PDF", "WORD", "EPUB", "TXT", etc.
)