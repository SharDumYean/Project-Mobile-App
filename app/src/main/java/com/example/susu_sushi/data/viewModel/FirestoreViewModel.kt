package com.example.susu_sushi.data.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.susu_sushi.data.model.Category
import com.example.susu_sushi.data.repository.FirestoreRepository
import kotlinx.coroutines.launch

class FirestoreViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: State<List<Category>> = _categories

    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
                Log.d("Category", "Fetching categories...")
                val result = repository.getCategories()
                Log.d("Category", "Category: ${result}")
                _categories.value = result
        }
    }
}
