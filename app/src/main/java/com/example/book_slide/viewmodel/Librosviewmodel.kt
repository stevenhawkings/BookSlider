package com.example.book_slide.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.book_slide.DataClasses.Book.Libro
import com.example.book_slide.DataClasses.Book.LibroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrosViewModel @Inject constructor(
    private val repository: LibroRepository
) : ViewModel() {

    val libros: StateFlow<List<Libro>> = repository.libros
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun agregar(uri: Uri, nombre: String, tipo: String) {
        viewModelScope.launch { repository.agregar(uri, nombre, tipo) }
    }

    fun eliminar(libro: Libro) {
        viewModelScope.launch { repository.eliminar(libro) }
    }
}