package com.example.finalproject.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalproject.data.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM User WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM User WHERE email = :email LIMIT 1")
    fun getUserByEmailAccount(email: String): LiveData<User>
}
