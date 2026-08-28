package com.example.book_slide.DataClasses.Users

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsersDao {

    @Insert
    fun insert(users: Users)

    //@Query("DELETE FROM users WHERE id = :id") cambiar por id proximamente.
    fun delete(users: Users)

    @Update
    fun update(users: Users)

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<Users>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun findById(id: Int): Users
}