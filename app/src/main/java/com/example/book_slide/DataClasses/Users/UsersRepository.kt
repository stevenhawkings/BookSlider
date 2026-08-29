package com.example.book_slide.DataClasses.Users

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

open class UsersRepository(private val UsersDao: UsersDao){
    val allUsers: LiveData<List<Users>> = UsersDao.getAll()

    suspend fun insert(Users: Users) {
        UsersDao.insert(Users)
    }

    suspend fun update(users: Users) {
        UsersDao.update(users)
    }

    suspend fun delete(users: Users) {
        UsersDao.delete(users)
    }

    suspend fun login(email: String, password: String): Users? {
        return UsersDao.login(email, password)
    }
}