package com.example.book_slide.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.book_slide.data.repository.SSORepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SSOState {
    object Idle : SSOState
    object CheckingExistingSession : SSOState
    data class Authenticated(val user: FirebaseUser) : SSOState
    data class Error(val message: String) : SSOState
}

class SSOViewModel(
    private val repository: SSORepositoryImpl = SSORepositoryImpl(),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _ssoState = MutableStateFlow<SSOState>(SSOState.Idle)
    val ssoState: StateFlow<SSOState> = _ssoState.asStateFlow()

    fun checkExistingSession(context: Context) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _ssoState.value = SSOState.Authenticated(currentUser)
        } else {
            _ssoState.value = SSOState.CheckingExistingSession
            viewModelScope.launch {
                repository.silentSSO(context)
                    .onSuccess { user ->
                        _ssoState.value = SSOState.Authenticated(user)
                    }
                    .onFailure {
                        _ssoState.value = SSOState.Idle
                    }
            }
        }
    }

    fun initiateInteractiveSSO(context: Context) {
        _ssoState.value = SSOState.CheckingExistingSession
        viewModelScope.launch {
            repository.interactiveSSO(context)
                .onSuccess { user ->
                    _ssoState.value = SSOState.Authenticated(user)
                }
                .onFailure { exception ->
                    _ssoState.value = SSOState.Error(exception.message ?: "Error desconocido")
                }
        }
    }

    fun signOut(context: Context) {
        firebaseAuth.signOut()
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
            } catch (e: Exception) {
                // Manejo silencioso del error al limpiar estado de credenciales
            }
            _ssoState.value = SSOState.Idle
        }
    }
}
