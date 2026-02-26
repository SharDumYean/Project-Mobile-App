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
import com.example.susu_sushi.pages.CategoryScreen
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
                // State to manage current screen
                var currentScreen by remember { mutableStateOf("category") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "login" -> LoginScreen(
                                onNavigateToSignUp = { currentScreen = "signup" }
                            )
                            "signup" -> SignUpScreen(
                                onNavigateToLogin = { currentScreen = "login" }
                            )
                            "category" -> CategoryScreen()

                        }
                    }
                }
            }
        }
    }
}
