package com.example.book_slide.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.book_slide.DataClasses.Book.Libro
import com.example.book_slide.viewmodel.LibrosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaLibrosScreen(
    viewModel: LibrosViewModel = hiltViewModel(),
    onAgregar: () -> Unit
) {
    val libros by viewModel.libros.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.semantics { contentDescription = "Agregar nuevo archivo" }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Barra de búsqueda que antes vivía en fragment_home2.xml (searchCompose).
            DocumentationSearchBar()

            if (libros.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay archivos agregados todavía")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(libros, key = { it.id }) { libro ->
                        ItemDeslizable(libro = libro, onEliminar = { viewModel.eliminar(libro) })
                    }
                }
            }
        }
    }
}

/** Punto 2: swipe hacia la izquierda revela un fondo rojo y elimina el item. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemDeslizable(libro: Libro, onEliminar: () -> Unit) {
    val estadoDismiss = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onEliminar()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = estadoDismiss,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = 20.dp)
                    .semantics { contentDescription = "Deslizando para eliminar ${libro.nombre}" },
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
            }
        }
    ) {
        ItemAnimadoEntrada { EventCard(libro) }
    }
}

/** Punto 3: animación de entrada con animateDpAsState (offset) + fade al aparecer el item. */
@Composable
private fun ItemAnimadoEntrada(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    val desplazamiento by animateDpAsState(
        targetValue = if (visible) 0.dp else 40.dp,
        label = "desplazamientoEntrada"
    )
    val opacidad by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        label = "opacidadEntrada"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .offset(y = desplazamiento)
            .graphicsLayer(alpha = opacidad)
    ) {
        content()
    }
}
