package com.example.book_slide.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.book_slide.DataClasses.Book.LibroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroDao {

    @Query("SELECT * FROM libros ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<LibroEntity>>

    @Insert
    suspend fun insertar(libro: LibroEntity): Long

    @Delete
    suspend fun eliminar(libro: LibroEntity)
}