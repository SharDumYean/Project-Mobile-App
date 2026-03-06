package com.example.susu_sushi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.susu_sushi.data.viewModel.AuthViewModel
import com.example.susu_sushi.data.viewModel.MenuViewModel
import com.example.susu_sushi.data.viewModel.SaveStateViewModel
import com.example.susu_sushi.pages.CartScreen
import com.example.susu_sushi.pages.CategoryScreen
import com.example.susu_sushi.pages.FoodDetailScreen
import com.example.susu_sushi.pages.HistoryScreen
import com.example.susu_sushi.pages.LoginScreen
import com.example.susu_sushi.pages.MenuScreen
import com.example.susu_sushi.pages.SettingScreen
import com.example.susu_sushi.pages.SignUpScreen
import com.example.susu_sushi.ui.theme.SUSU_SUSHITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SUSU_SUSHITheme {
                val navController = rememberNavController()
                val saveStateViewModel: SaveStateViewModel = viewModel()
                val menuViewModel: MenuViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "category",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                navController = navController ,
                                authViewModel = authViewModel
                            )
                        }

                        composable("signup") {
                            SignUpScreen(
                                navController = navController ,
                                authViewModel = authViewModel
                            )
                        }

                        composable("category") {
                            CategoryScreen(
                                navController = navController,
                                onNavigateToMenu = {categoryId ->
                                    navController.navigate("menu/${categoryId}")
                                },
                                saveStateViewModel = saveStateViewModel ,
                                menuViewModel = menuViewModel ,
                                authViewModel = authViewModel ,
                            )
                        }

                        composable("menu/{categoryId}") { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId")
                            MenuScreen(
                                navController = navController,
                                categoryId = categoryId ,
                                saveStateViewModel = saveStateViewModel ,
                                menuViewModel = menuViewModel ,
                                authViewModel = authViewModel
                            )
                        }

                        composable("addFood"){
                            FoodDetailScreen(
                                navController = navController,
                                onNavigateToMenu = {categoryId ->
                                    navController.navigate("menu/${categoryId}")
                                } ,
                                saveStateViewModel = saveStateViewModel,
                                authViewModel = authViewModel
                            )
                        }

                        composable("cart"){
                            CartScreen(
                                navController = navController ,
                                saveStateViewModel = saveStateViewModel ,
                                menuViewModel = menuViewModel ,
                            )
                        }

                        composable("history"){
                            HistoryScreen(
                                navController = navController ,
                                menuViewModel = menuViewModel,
                                authViewModel = authViewModel
                            )
                        }

                        composable("setting"){
                            SettingScreen(
                                navController = navController ,
                                authViewModel = authViewModel
                            )
                        }

                    }
                }
            }
        }
    }
}
