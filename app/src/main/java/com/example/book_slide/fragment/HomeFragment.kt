package com.example.book_slide.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.example.book_slide.ui.LibrosNavGraph
import dagger.hilt.android.AndroidEntryPoint

/**
 * OJO: ya no infla fragment_home2.xml ni usa RecyclerView/LibroAdapter.
 * Ahora hostea el NavGraph de Compose completo (lista + formulario), que es
 * donde están implementadas las animaciones (EventCard, entrada de items, swipe).
 *
 * Requiere @AndroidEntryPoint porque ListaLibrosScreen usa hiltViewModel().
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val esquemaOscuro = darkColorScheme(
        background = Color.Black,
        surface = Color.Black,
        surfaceVariant = Color(0xFF1C1C1C), // gris muy oscuro para las tarjetas, para que se distingan del fondo
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = Color.White
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(colorScheme = esquemaOscuro) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LibrosNavGraph()
                    }
                }
            }
        }
    }
}
