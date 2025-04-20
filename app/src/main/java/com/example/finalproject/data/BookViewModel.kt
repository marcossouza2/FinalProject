package com.example.finalproject.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.finalproject.dao.BookDao
import com.example.finalproject.data.entities.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val bookDao: BookDao = AppDatabase.getDatabase(application).bookDao()

    // Get all books from the database as LiveData
    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()

    // Add a new book
    fun insertBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            bookDao.insert(book)
        }
    }
}
