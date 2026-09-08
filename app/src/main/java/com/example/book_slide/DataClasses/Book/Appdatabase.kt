package com.example.book_slide.DataClasses.Book

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.book_slide.DataClasses.Book.LibroEntity
import com.example.book_slide.data.LibroDao

@Database(entities = [LibroEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun libroDao(): LibroDao
}

