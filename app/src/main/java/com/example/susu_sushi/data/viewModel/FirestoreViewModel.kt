package com.example.susu_sushi.data.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.susu_sushi.data.model.Category
import com.example.susu_sushi.data.model.Food
import com.example.susu_sushi.data.model.HistoryItem
import com.example.susu_sushi.data.model.OrderItem
import com.example.susu_sushi.data.repository.FirestoreRepository
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: State<List<Category>> = _categories

    private val _foods = mutableStateOf<List<Food>>(emptyList())
    val foods: State<List<Food>> = _foods

    private val _isSucces = mutableStateOf<Boolean>(false)
    val isSucces: State<Boolean> = _isSucces

    private val _historyOrders = mutableStateOf<List<HistoryItem>>(emptyList())
    val historyOrders: State<List<HistoryItem>> = _historyOrders


    init {
        getCategories()
        getFoods()
    }

    private fun getCategories() {
        viewModelScope.launch {
                Log.d("Category", "Fetching categories...")
                val result = repository.getCategories()
                Log.d("Category", "Category: ${result}")
                _categories.value = result
        }
    }

    private fun getFoods() {
        viewModelScope.launch {
            Log.d("Category", "Fetching categories...")
            val result = repository.getFoods()
            Log.d("Category", "Category: ${result}")
            _foods.value = result
        }
    }

    fun getFoodByCategory(catagoryId:String) :List<Food> {
        return _foods.value.filter { it.category_id == catagoryId }
    }

    fun getFoodByid(foodId: String): Food? {
        return _foods.value.find { it.id == foodId }
    }

    fun orderFood(items: List<OrderItem>, totalPrice: Double) {
        viewModelScope.launch {
            val result = repository.postOrder(items, totalPrice)
            _isSucces.value = result
        }
    }

    fun getHistory(userId: String) {
        viewModelScope.launch {
            Log.d("historyOrder", "Fetching history...")
            val result = repository.getOrdersByUserId(userId)
            _historyOrders.value = result
            Log.d("historyOrder", "historyOrder: ${_historyOrders.value}")
        }
    }

    fun clearHistory(userId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.deleteAllOrdersByUserId(userId)
            if (success) {
                _historyOrders.value = emptyList()
                onSuccess()
            }
        }
    }
}
