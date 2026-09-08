package com.example.book_slide.DataClasses.Book

import android.net.Uri
import com.example.book_slide.data.LibroDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

interface LibroRepository {
    val libros: Flow<List<Libro>>
    suspend fun agregar(uri: Uri, nombre: String, tipo: String)
    suspend fun eliminar(libro: Libro)
}

class LibroRepositoryImpl @Inject constructor(
    private val dao: LibroDao
) : LibroRepository {

    override val libros: Flow<List<Libro>> = dao.obtenerTodos().map { lista ->
        lista.map { Libro(id = it.id, uri = Uri.parse(it.rutaLocal), nombre = it.nombre, tipo = it.tipo) }
    }

    override suspend fun agregar(uri: Uri, nombre: String, tipo: String) {
        dao.insertar(LibroEntity(rutaLocal = uri.toString(), nombre = nombre, tipo = tipo))
    }

    override suspend fun eliminar(libro: Libro) {
        AlmacenamientoArchivos.eliminarArchivoFisico(libro.uri)
        dao.eliminar(LibroEntity(id = libro.id, rutaLocal = libro.uri.toString(), nombre = libro.nombre, tipo = libro.tipo))
    }
}