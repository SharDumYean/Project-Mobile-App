package com.example.susu_sushi.data.model

import com.google.firebase.firestore.DocumentId

data class OrderList(
    val foodId: String = "",
    val quantity: Int = 0,
    val note: String = ""
)

data class HistoryItem(
    @DocumentId val id: String = "" ,
    val orderId: String = "",
    val userId: String = "",
    val orderList: List<OrderList> = emptyList(),
    val totalPrice: Double = 0.0,
    val createdAt: Long = 0L
)
