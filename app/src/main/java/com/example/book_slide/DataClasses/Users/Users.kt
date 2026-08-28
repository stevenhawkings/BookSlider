package com.example.book_slide.DataClasses.Users

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class Users (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "Nombre")
    var name: String? = null,

    @ColumnInfo(name = "Email")
    var email: String? = null,

    @ColumnInfo(name = "Contraseña")
    var password: String? = null,

)