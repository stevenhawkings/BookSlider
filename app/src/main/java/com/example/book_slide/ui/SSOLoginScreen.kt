package com.example.book_slide.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.book_slide.viewmodel.SSOState
import com.example.book_slide.viewmodel.SSOViewModel

@Composable
fun SSOLoginScreen(viewModel: SSOViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.ssoState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkExistingSession(context)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is SSOState.CheckingExistingSession -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is SSOState.Authenticated -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Bienvenido, ${currentState.user.displayName}", color = MaterialTheme.colorScheme.onSurface)
                        Button(onClick = { viewModel.signOut(context) }) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
            }
            is SSOState.Idle, is SSOState.Error -> {
                Button(
                    onClick = { viewModel.initiateInteractiveSSO(context) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Iniciar sesión con Google SSO")
                }
            }
        }
    }
}
