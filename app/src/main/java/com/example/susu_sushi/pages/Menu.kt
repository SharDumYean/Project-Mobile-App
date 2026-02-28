package com.example.susu_sushi.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.susu_sushi.R
import com.example.susu_sushi.components.BackPageButton
import com.example.susu_sushi.components.MainScaffold
import com.example.susu_sushi.data.model.Food
import com.example.susu_sushi.data.viewModel.FoodViewModel
import com.example.susu_sushi.data.viewModel.SaveStateViewModel
import com.example.susu_sushi.ui.theme.SUSU_SUSHITheme

@Composable
fun MenuScreen(
    categoryId: String? ,
    onNavigateToCategory: () -> Unit ,
    onNavigateToAddPage: () -> Unit ,
    saveStateViewModel: SaveStateViewModel ,
) {
    val foodViewModel: FoodViewModel = viewModel()
    val foods by foodViewModel.foods

    LaunchedEffect(categoryId) {
        if (categoryId.isNullOrEmpty()) {
            Log.e("MenuScreen", "categoryId is null or empty")
        } else {
            foodViewModel.getFoodByCategory(categoryId)
        }
    }

    MainScaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            BackPageButton(onNavigateToCategory)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),

                ) {
                items(foods) { food ->
                    FoodItemCard(
                        foodDetail = food,
                        toAddFoodPage = { foodDetail ->
                            saveStateViewModel.setFood(foodDetail)
                            Log.d("saveStateFood", "Food: ${saveStateViewModel.food}")
                            onNavigateToAddPage()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    foodDetail: Food,
    toAddFoodPage: (Food) -> Unit ,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(onClick = {
                toAddFoodPage(foodDetail)
            })
        ,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = foodDetail.image_url,
                contentDescription = foodDetail.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = foodDetail.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "฿ ${"%.2f".format(foodDetail.price)}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
//    SUSU_SUSHITheme {
//        MenuScreen("Sushi")
//    }
}
