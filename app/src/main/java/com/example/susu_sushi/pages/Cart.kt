package com.example.susu_sushi.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.susu_sushi.data.model.OrderItem
import com.example.susu_sushi.data.viewModel.AuthViewModel
import com.example.susu_sushi.data.viewModel.MenuViewModel
import com.example.susu_sushi.data.viewModel.SaveStateViewModel
import com.example.susu_sushi.ui.theme.SushiRed
import kotlinx.coroutines.coroutineScope

@Composable
fun CartScreen(
    navController: NavHostController,
    saveStateViewModel: SaveStateViewModel ,
    menuViewModel: MenuViewModel
) {
    val myItemList by remember { saveStateViewModel.orderItems }
    val totalPrice = myItemList.sumOf { it.food.price * it.quantity }
    val isOrderSucces by menuViewModel.isSucces

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(SushiRed)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.popBackStack() }
                )
                
                Text(
                    text = "ตะกร้า",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(myItemList) { item ->
                    CartItemCard(item, saveStateViewModel , navController)
                }
            }

            // Bottom Summary Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "รวมทั้งหมด",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "฿ ${String.format("%.2f", totalPrice)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if(!myItemList.isEmpty()) {
                                menuViewModel.orderFood(myItemList, totalPrice)
                                saveStateViewModel.clearCart()
                                navController.navigate("history")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SushiRed)
                    ) {
                        Text(
                            text = "สั่งรายการสินค้า",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: OrderItem,
    saveStateViewModel: SaveStateViewModel ,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = item.food.image_url,
                    contentDescription = item.food.name,
                    modifier = Modifier
                        .size(90.dp, 65.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.food.name.uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "฿ ${String.format("%.2f", item.food.price * item.quantity)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (item.note.isNotEmpty()) {
                        Text(
                            text = "-${item.note}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "แก้ไข",
                        fontSize = 14.sp,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable {
                                saveStateViewModel.setFood(item)
                                saveStateViewModel.deleteOrderitem(item)
                                navController.navigate("addFood")
                            }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "-",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable {
                            saveStateViewModel.minusOrderItemQuantity(item)
                        }
                )
                Text(
                    text = item.quantity.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "+",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clickable {
                            saveStateViewModel.addOrderItemQuantity(item)
                        }
                )
            }
        }
    }
}
