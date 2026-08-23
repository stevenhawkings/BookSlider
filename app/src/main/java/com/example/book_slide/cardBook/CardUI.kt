package com.example.book_slide.cardBook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable

fun minimalCard() {
    Card(
        onClick = { /* Se a leido el archivo */},
        modifier = Modifier.size(width = 180.dp, height = 100.dp),
    ) {
        Box(Modifier.fillMaxSize()) { Text("Clicklabe", Modifier.align(Alignment.Center))}
    }
}