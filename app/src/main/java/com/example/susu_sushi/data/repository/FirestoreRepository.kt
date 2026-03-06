package com.example.susu_sushi.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.susu_sushi.data.model.Category
import com.example.susu_sushi.data.model.Food
import com.example.susu_sushi.data.model.HistoryItem
import com.example.susu_sushi.data.model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
            Log.e("getFoods", "Error fetching foods", e)
            emptyList()
        }
    }

    suspend fun postOrder(items: List<OrderItem>, totalPrice: Double): Boolean {
        return try {
            val currentUser = auth.currentUser ?: return false
            val userId = currentUser.uid
            val orderRef = db.collection("orders").document()

            val orderList =  items.map {  hashMapOf(
                "foodId" to it.food.id,
                "quantity" to it.quantity,
                "note" to it.note
            )  }

            val orderItem = hashMapOf(
                "orderId"    to orderRef.id,
                "userId"     to userId,
                "orderList"    to orderList ,
                "totalPrice" to totalPrice,
                "createdAt"  to System.currentTimeMillis()
            )

            orderRef.set(orderItem).await()
            true
        } catch (e: Exception) {
            Log.e("postOrder", "Error posting order", e)
            false
        }
    }

    suspend fun getOrdersByUserId(userId: String): List<HistoryItem> {
        return try {
            val result = db.collection("orders")
                .whereEqualTo("userId", userId)
//                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            result.toObjects(HistoryItem::class.java)
        } catch (e: Exception) {
            Log.e("getOrdersByUserId", "Error fetching orders", e)
            emptyList()
        }
    }

    suspend fun deleteAllOrdersByUserId(userId: String): Boolean {
        return try {
            val result = db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val batch = db.batch()
            for (document in result.documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("deleteAllOrders", "Error deleting orders", e)
            false
        }
    }
}
