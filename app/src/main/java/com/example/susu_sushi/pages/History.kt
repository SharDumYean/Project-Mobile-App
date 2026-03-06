package com.example.susu_sushi.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.susu_sushi.components.BackPageButton
import com.example.susu_sushi.components.MainScaffold
import com.example.susu_sushi.data.model.HistoryItem
import com.example.susu_sushi.data.viewModel.AuthViewModel
import com.example.susu_sushi.data.viewModel.MenuViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavHostController,
    menuViewModel: MenuViewModel,
    authViewModel: AuthViewModel
) {
    val historyOrders by menuViewModel.historyOrders
    val authState by authViewModel.authState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.LoggedIn) {
            menuViewModel.getHistory((authState as AuthViewModel.AuthState.LoggedIn).user.uid)
        }
    }

    MainScaffold(navController, authViewModel) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8F8F8))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    BackPageButton({ navController.popBackStack() })
                    Text(
                        text = "Order History",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                if (historyOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No history found", color = Color.Gray)
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(historyOrders) { order ->
                                HistoryOrderCard(order, menuViewModel)
                            }
                        }

                        // Clear History Button (เช็คบิล)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    if (authState is AuthViewModel.AuthState.LoggedIn) {
                                        menuViewModel.clearHistory(
                                            userId = (authState as AuthViewModel.AuthState.LoggedIn).user.uid,
                                            onSuccess = { showSuccessDialog = true }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .border(2.dp, Color(0xFF81C784), RoundedCornerShape(30.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA5D6A7)),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Text(
                                    text = "เช็คบิล",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (showSuccessDialog) {
                Dialog(onDismissRequest = { showSuccessDialog = false }) {
                    Card(
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7CB342)) // Matching green from image
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Close Button
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Black,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                                    .clickable { showSuccessDialog = false }
                            )

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Checkmark Icon in Circle
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color(0xFF4CAF50), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Success",
                                        tint = Color(0xFF8BC34A),
                                        modifier = Modifier.size(64.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "เสร็จสิ้น",
                                    color = Color.Black,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryOrderCard(order: HistoryItem, menuViewModel: MenuViewModel) {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(order.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Date and Order ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = dateString, fontSize = 12.sp, color = Color.Gray)
                Text(text = "#${order.id.take(8)}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List of items in this order
            order.orderList.forEach { item ->
                val food = menuViewModel.getFoodByid(item.foodId)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (food != null) {
                        AsyncImage(
                            model = food.image_url,
                            contentDescription = food.name,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = food.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (item.note.isNotEmpty()) {
                                Text(text = "- ${item.note}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Text(text = "x${item.quantity}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

            // Footer: Total Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Amount", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Text(
                    text = "฿ ${String.format("%.2f", order.totalPrice)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFFD32F2F)
                )
            }
        }
    }
}
