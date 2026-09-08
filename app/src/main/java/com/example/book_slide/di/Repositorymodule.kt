package com.example.book_slide.di

import com.example.book_slide.DataClasses.Book.LibroRepository
import com.example.book_slide.DataClasses.Book.LibroRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindLibroRepository(impl: LibroRepositoryImpl): LibroRepository
}