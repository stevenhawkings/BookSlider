package com.example.book_slide.DataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Users(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mail: String,
    val password: String
)