package com.example.finalproject.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.entities.User
import com.example.finalproject.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao: UserDao = AppDatabase.getDatabase(application).userDao()

    // Register new user
    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    // Validate user credentials
    fun validateLogin(email: String, password: String, callback: (User?) -> Unit) {
        // Launch a coroutine to perform the query in the background
        viewModelScope.launch {
            try {
                // Check for user by email and password
                val user = userDao.getUserByEmailAndPassword(email, password)
                callback(user) // Return the user if found, else null
            } catch (e: Exception) {
                // Log the error or show an error message
                Log.e("UserViewModel", "Error validating login", e)
                callback(null) // If an error occurs, return null
            }
        }
    }

}
