package com.example.finalproject.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalproject.data.entities.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM Book WHERE email = :email")
    fun getBooksByUsername(email: String): Flow<List<Book>>

    @Query("SELECT * FROM Book")
    fun getAllBooks(): LiveData<List<Book>>
}