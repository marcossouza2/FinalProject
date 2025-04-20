package com.example.finalproject.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.entities.User
import com.example.finalproject.dao.UserDao
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao: UserDao = AppDatabase.getDatabase(application).userDao()

    fun validateLogin(email: String, password: String, callback: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = userDao.getUserByEmailAndPassword(email, password)
                callback(user)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error validating login", e)
                callback(null)
            }
        }
    }

}
