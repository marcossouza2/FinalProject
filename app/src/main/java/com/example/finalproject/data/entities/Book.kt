package com.example.finalproject.data.entities

import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["email"],
        childColumns = ["email"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val year: Int,
    val price: Double,
    val genre: String,
    val email: String
)