package com.example.finalproject.data.entities

import androidx.room.*

@Entity
data class User(
    @PrimaryKey val email: String,
    val username: String,
    val password: String,
    val address: String,
    val phone: String
)