package com.example.susu_sushi.data.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.susu_sushi.data.model.Food
import com.example.susu_sushi.data.model.OrderItem
import kotlin.collections.minus
import kotlin.collections.plus

class SaveStateViewModel : ViewModel(){
    private val _orderItem = mutableStateOf(OrderItem());
    val orderItem: State<OrderItem> = _orderItem

    private val _categoryId = mutableStateOf<String>("");
    val categoryId: State<String> = _categoryId

    private val _orderItems = mutableStateOf<List<OrderItem>>(emptyList())
    val orderItems: State<List<OrderItem>> = _orderItems


    fun setFood(orderItem: OrderItem){
        _orderItem.value = orderItem;
    }

    fun setCategoryId(categoryId: String){
        _categoryId.value = categoryId;
    }

    fun addOrderItem(orderItem: OrderItem){
        _orderItems.value += orderItem;
    }

    fun addOrderItemQuantity(orderItem: OrderItem){
        val index = orderItems.value.indexOf(orderItem)
        val temp = orderItems.value.toMutableList()
        temp[index] = temp[index].copy(quantity = _orderItems.value[index].quantity + 1)
        _orderItems.value = temp

    }

    fun minusOrderItemQuantity(orderItem: OrderItem){
        val index = orderItems.value.indexOf(orderItem)
        val quantity = _orderItems.value[index].quantity
        if (quantity > 1){
            val temp = orderItems.value.toMutableList()
            temp[index] = temp[index].copy(quantity = _orderItems.value[index].quantity - 1)
            _orderItems.value = temp
        }
        else{
            _orderItems.value -= orderItem;
        }
    }

    fun deleteOrderitem(orderItem: OrderItem){
        _orderItems.value -= orderItem;
    }

    fun clearCart(){
        _orderItems.value = emptyList()
    }

}