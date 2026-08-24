package com.example.book_slide.cardBook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MinimalCard() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Mis archivos")
    }
}
