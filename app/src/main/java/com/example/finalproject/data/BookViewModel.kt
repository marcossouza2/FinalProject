package com.example.finalproject.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.finalproject.dao.BookDao
import com.example.finalproject.data.entities.Book

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val bookDao: BookDao = AppDatabase.getDatabase(application).bookDao()

    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()
}
