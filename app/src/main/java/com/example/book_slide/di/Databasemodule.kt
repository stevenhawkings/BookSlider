package com.example.book_slide.di

import android.content.Context
import androidx.room.Room
import com.example.book_slide.DataClasses.Book.AppDatabase
import com.example.book_slide.data.LibroDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "libros_db").build()

    @Provides
    fun provideLibroDao(db: AppDatabase): LibroDao = db.libroDao()
}