package com.bloomtregua.rgb.viewmodels // o il tuo package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bloomtregua.rgb.database.categories.CategoryDao

class CategoriesViewModelFactory(private val categoryDao: CategoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriesViewModel(categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}