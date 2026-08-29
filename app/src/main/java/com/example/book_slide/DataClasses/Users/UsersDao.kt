package com.example.book_slide.DataClasses.Users

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.sqlite.driver.AndroidSQLiteDriver

@Dao
interface UsersDao {

    @Insert
    suspend fun insert(users: Users)

    //@Query("DELETE FROM users WHERE id = :id") cambiar por id proximamente.
    @Delete
    suspend fun delete(users: Users)

    @Update
    suspend fun update(users: Users)

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<Users>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun findById(id: Int): Users

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Users?
}


@Database(entities = [Users::class], version = 1)
abstract class userDatabase : RoomDatabase() {
    abstract fun UsersDao() : UsersDao
}

class UsersDatabase {
    fun getUserDb(applicationContext: Context): userDatabase {
        val db = Room.databaseBuilder(applicationContext,
            userDatabase::class.java,
            "usuarios")
            .build()
        return db
    }
}