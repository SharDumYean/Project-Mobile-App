package com.example.susu_sushi.data.model

data class OrderItem(
    val food: Food = Food() ,
    var quantity: Int = 1 ,
    var note: String = "" ,
)
