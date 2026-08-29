package com.example.book_slide.DataClasses.Users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

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
}