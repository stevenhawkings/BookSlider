package com.example.book_slide.util

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ColorBlindnessMode(
    val id: String,
    val title: String,
    val description: String,
    val primaryColorHex: Int,
    val primaryColorCompose: Color,
    val surfaceVariantCompose: Color
) {
    NORMAL(
        id = "normal",
        title = "Normal (Sin filtro)",
        description = "Paleta de colores estándar (Cian)",
        primaryColorHex = 0xFF00F5D4.toInt(),
        primaryColorCompose = Color(0xFF00F5D4),
        surfaceVariantCompose = Color(0xFF1C1C1C)
    ),
    PROTANOPIA(
        id = "protanopia",
        title = "Protanopía",
        description = "Dificultad con el color rojo (Azul de alto contraste)",
        primaryColorHex = 0xFF56B4E9.toInt(),
        primaryColorCompose = Color(0xFF56B4E9),
        surfaceVariantCompose = Color(0xFF1B2A38)
    ),
    DEUTERANOPIA(
        id = "deuteranopia",
        title = "Deuteranopía",
        description = "Dificultad con el color verde (Dorado naranja brillante de alto contraste)",
        primaryColorHex = 0xFFE69F00.toInt(),
        primaryColorCompose = Color(0xFFE69F00),
        surfaceVariantCompose = Color(0xFF332612)
    ),
    TRITANOPIA(
        id = "tritanopia",
        title = "Tritanopía",
        description = "Dificultad con el color azul (Rojo de alto contraste)",
        primaryColorHex = 0xFFD55E00.toInt(),
        primaryColorCompose = Color(0xFFD55E00),
        surfaceVariantCompose = Color(0xFF331612)
    );

    fun toComposeColorScheme(): ColorScheme {
        return darkColorScheme(
            primary = primaryColorCompose,
            background = Color.Black,
            surface = Color.Black,
            surfaceVariant = surfaceVariantCompose,
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Color.White,
            onPrimary = Color.Black
        )
    }

    companion object {
        fun fromId(id: String?): ColorBlindnessMode {
            return entries.firstOrNull { it.id == id } ?: NORMAL
        }
    }
}

object ColorBlindnessManager {
    private const val PREFS_NAME = "book_slide_prefs"
    private const val KEY_COLORBLIND_MODE = "colorblind_mode"

    private val _currentMode = MutableStateFlow(ColorBlindnessMode.NORMAL)
    val currentMode: StateFlow<ColorBlindnessMode> = _currentMode.asStateFlow()

    fun init(context: Context): ColorBlindnessMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedId = prefs.getString(KEY_COLORBLIND_MODE, ColorBlindnessMode.NORMAL.id)
        val mode = ColorBlindnessMode.fromId(savedId)
        _currentMode.value = mode
        return mode
    }

    fun setMode(context: Context, mode: ColorBlindnessMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_COLORBLIND_MODE, mode.id).apply()
        _currentMode.value = mode
    }

    fun getMode(context: Context): ColorBlindnessMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedId = prefs.getString(KEY_COLORBLIND_MODE, ColorBlindnessMode.NORMAL.id)
        val mode = ColorBlindnessMode.fromId(savedId)
        _currentMode.value = mode
        return mode
    }
}
