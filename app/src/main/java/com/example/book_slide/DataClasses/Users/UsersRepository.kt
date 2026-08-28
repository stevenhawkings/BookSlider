package com.example.book_slide.DataClasses.Users

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class UsersRepository(private val UsersDao: UsersDao) {
    val allUsers: LiveData<List<Users>> = UsersDao.getAll()

    suspend fun insert(Users: UsersDao) {
        UsersDao.insert(Users())
    }

    suspend fun update(users: Users) {
        UsersDao.update(Users())
    }

    suspend fun delete(users: Users) {
        UsersDao.delete(users)
    }
}