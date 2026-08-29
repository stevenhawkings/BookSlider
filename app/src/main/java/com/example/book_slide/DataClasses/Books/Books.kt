package com.example.book_slide.DataClasses.Books

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Books")
data class Books (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "Titulo")
    var titulo: String? = null,

    @ColumnInfo(name = "Descripcion")
    var descripcion: String? = null,

    @ColumnInfo(name = "Pagina")
    var pagina: Int? = null,
)