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
import com.example.susu_sushi.data.viewModel.SaveStateViewModel
import com.example.susu_sushi.pages.CategoryScreen
import com.example.susu_sushi.pages.FoodDetailScreen
import com.example.susu_sushi.pages.LoginScreen
import com.example.susu_sushi.pages.MenuScreen
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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "category",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(onNavigateToSignUp = { navController.navigate("signup") })
                        }

                        composable("signup") {
                            SignUpScreen(onNavigateToLogin = { navController.navigate("login") })
                        }

                        composable("category") {
                            CategoryScreen(
                                onNavigateToMenu = {categoryId ->
                                    navController.navigate("menu/${categoryId}")
                                } ,
                                saveStateViewModel = saveStateViewModel
                            )
                        }

                        composable("menu/{categoryId}") { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId")
                            MenuScreen(
                                categoryId = categoryId ,
                                onNavigateToCategory = {navController.navigate("category")} ,
                                onNavigateToAddPage = {navController.navigate("addFood")} ,
                                saveStateViewModel = saveStateViewModel
                            )
                        }

                        composable("addFood"){
                            FoodDetailScreen(
                                onNavigateToMenu = {categoryId ->
                                    navController.navigate("menu/${categoryId}")
                                } ,
                                saveStateViewModel = saveStateViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
