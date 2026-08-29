package com.example.book_slide.DataClasses.Books

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update


@Dao
interface BooksDao {

    @Insert
    fun insert(books: Books)

    @Update
    fun update(books: Books)

    @Query("DELETE FROM books WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM books")
    fun all(): LiveData<List<Books>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun findById(id: Int): Books
}
