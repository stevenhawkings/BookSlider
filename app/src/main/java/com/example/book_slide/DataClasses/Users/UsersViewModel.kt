package com.example.book_slide.DataClasses.Users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class UsersViewModel (
    private val repository: UsersRepository
) : ViewModel() {
    fun registerUser(users: Users) {
        viewModelScope.launch {
            repository.insert(users)
        }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.login(email, password)

            onResult(user != null)
        }
    }

    // Dentro de UsersViewModel class:
    fun loginWithGoogleToken(idToken: String, onResult: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }
}

