package com.example.susu_sushi.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.susu_sushi.data.model.Category
import com.example.susu_sushi.data.model.Food
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // 🔹 ดึง Categories
    suspend fun getCategories(): List<Category> {
        return try {
            val result = db.collection("categories").get().await()
            result.toObjects(Category::class.java)
        } catch (e: Exception) {
            Log.e("getCategories", "Error fetching categories", e)
            emptyList()
        }
    }

    suspend fun getFoods(): List<Food> {
        return try {
            val result = db.collection("foods").get().await()
            result.toObjects(Food::class.java)
        } catch (e: Exception) {
            Log.e("getFoods", "Error fetching categories", e)
            emptyList()
        }

        }

}
