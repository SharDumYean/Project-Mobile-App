package com.example.susu_sushi.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.susu_sushi.components.BackPageButton
import com.example.susu_sushi.components.MainScaffold
import com.example.susu_sushi.data.model.OrderItem
import com.example.susu_sushi.data.viewModel.SaveStateViewModel
import com.example.susu_sushi.ui.theme.SushiRed

@Composable
fun FoodDetailScreen(
    navController: NavHostController,
    onNavigateToMenu: (String)-> Unit,
    saveStateViewModel: SaveStateViewModel,
) {
    MainScaffold(navController) { innerPadding ->
        val orderItem = saveStateViewModel.orderItem.value
        var quantity by remember { mutableIntStateOf(orderItem.quantity) }
        var note by remember { mutableStateOf(orderItem.note) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            BackPageButton({ onNavigateToMenu(saveStateViewModel.categoryId.value) })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 24.dp),
            ) {

                // รูปภาพอาหารพร้อมกรอบ
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AsyncImage(
                        model = orderItem.food.image_url,
                        contentDescription = orderItem.food.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ชื่ออาหารและราคา
                Text(
                    text = orderItem.food.name.uppercase(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Text(
                    text = "฿ ${String.format("%.2f", orderItem.food.price)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                // ช่องกรอกรายละเอียดเพิ่มเติม
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("รายละเอียดเพิ่มเติม", color = Color(0xFFD32F2F)) },
                    placeholder = { Text("หิวครับเชฟ", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedBorderColor = Color(0xFFD32F2F),
                        cursorColor = Color(0xFFD32F2F)
                    )
                )

                // ส่วนเลือกจำนวนอาหาร
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "-",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .clickable { if (quantity > 1) quantity-- }
                            .padding(16.dp)
                    )
                    Text(
                        text = quantity.toString(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .clickable { quantity++ }
                            .padding(16.dp)
                    )
                }

                // ปุ่มเพิ่มลงในตะกร้า
                Button(
                    onClick = {
                        saveStateViewModel.addOrderItem(
                            OrderItem(
                                food = orderItem.food,
                                quantity = quantity,
                                note = note
                            )
                        )
                        navController.navigate("menu/${saveStateViewModel.categoryId.value}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SushiRed)
                ) {
                    Text(
                        text = "เพิ่มในตะกร้า",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}