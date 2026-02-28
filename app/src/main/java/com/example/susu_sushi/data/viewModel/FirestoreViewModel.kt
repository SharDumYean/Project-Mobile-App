package com.example.susu_sushi.data.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.susu_sushi.data.model.Category
import com.example.susu_sushi.data.model.Food
import com.example.susu_sushi.data.repository.FirestoreRepository
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
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

class FoodViewModel : ViewModel(){
    private val repository = FirestoreRepository()

    private val _foods = mutableStateOf<List<Food>>(emptyList())
    val foods: State<List<Food>> = _foods

    fun getFoodByCategory(CatagoryId:String){
        viewModelScope.launch {
            Log.d("Food", "Fetching categories...")
            val result = repository.getFoodsByCategory(CatagoryId)
            Log.d("Food", "Category: ${result}")
            _foods.value = result
        }
    }
}

class SaveStateViewModel : ViewModel(){
    private val _food = mutableStateOf<Food>(Food());
    val food: State<Food> = _food

    private val _categoryId = mutableStateOf<String>("");
    val categoryId: State<String> = _categoryId

    fun setFood(food: Food){
        _food.value = food;
    }

    fun setCategoryId(categoryId: String){
        _categoryId.value = categoryId;
    }
}
