package com.example.book_slide.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val RUTA_LISTA = "lista"
private const val RUTA_FORMULARIO = "formulario"

@Composable
fun LibrosNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = RUTA_LISTA) {
        composable(RUTA_LISTA) {
            ListaLibrosScreen(
                onAgregar = { navController.navigate(RUTA_FORMULARIO) }
            )
        }
        composable(RUTA_FORMULARIO) {
            FormularioLibroScreen(
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}