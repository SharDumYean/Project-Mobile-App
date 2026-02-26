package com.example.susu_sushi.data.model

import com.google.firebase.firestore.DocumentId

data class Category(
    @DocumentId val id: String = "", // ดึง "donburi", "drink" มาใส่ให้อัตโนมัติ
    val name: String = "",
    val image_url: String = ""
)
