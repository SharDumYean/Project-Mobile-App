package com.example.susu_sushi.data.model

import com.google.firebase.firestore.DocumentId

data class Food(
    @DocumentId val id: String = "" ,
    val name: String = "",
    val price: Double = 0.0,
    val category_id: String = "",
    val image_url: String = "",
    val available: Boolean = true
)
