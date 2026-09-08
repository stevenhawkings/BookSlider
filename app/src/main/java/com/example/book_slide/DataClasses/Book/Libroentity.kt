package com.example.book_slide.DataClasses.Book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros")
data class LibroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rutaLocal: String, // ruta del archivo YA COPIADO dentro de filesDir/libros
    val nombre: String,
    val tipo: String
)