package com.example.book_slide.Book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Se instancia con `by activityViewModels()` en HomeFragment y en addFragment,
 * así ambos comparten la misma lista mientras la Activity esté viva.
 */
class LibrosViewModel : ViewModel() {

    private val _libros = MutableLiveData<MutableList<Libro>>(mutableListOf())
    val libros: LiveData<MutableList<Libro>> = _libros

    fun agregar(libro: Libro) {
        val actual = _libros.value ?: mutableListOf()
        actual.add(libro)
        _libros.value = actual
    }

    fun eliminar(position: Int) {
        val actual = _libros.value ?: return
        if (position in actual.indices) {
            actual.removeAt(position)
            _libros.value = actual
        }
    }
}