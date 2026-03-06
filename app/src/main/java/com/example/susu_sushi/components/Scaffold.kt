package com.example.susu_sushi.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.remove
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.susu_sushi.R
import com.example.susu_sushi.data.viewModel.AuthViewModel
import com.example.susu_sushi.ui.theme.SushiRed
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val profileImageUrl by authViewModel.profileImageUrl.collectAsState() // ✅ ใช้ตัวนี้

    // เริ่ม/หยุด listener ตาม authState
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.LoggedIn) {
            val userId = (authState as AuthViewModel.AuthState.LoggedIn).user.uid
            authViewModel.startListeningProfile(userId)
        } else {
            authViewModel.stopListeningProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.sususushi_logo_white),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUSU SUSHI",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    // Cart Button
                    IconButton(onClick = {
                        if (authState is AuthViewModel.AuthState.LoggedIn) {
                            navController.navigate("cart")
                        } else {
                            navController.navigate("login")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Profile Button
                    IconButton(onClick = {
                        if (authState is AuthViewModel.AuthState.LoggedIn) {
                            navController.navigate("setting")
                        } else {
                            navController.navigate("login")
                        }
                    }) {
                        if (authState is AuthViewModel.AuthState.LoggedIn) {
                            if (profileImageUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(profileImageUrl)
                                        .diskCachePolicy(CachePolicy.DISABLED)
                                        .memoryCachePolicy(CachePolicy.DISABLED)
                                        .build(),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // ถ้าไม่มีรูปให้แสดงเป็นวงกลมสีดำ
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                            }
                        } else {
                            // ถ้ายังไม่ได้ Login ให้แสดงไอคอนปกติ
                            Icon(
                                painter = painterResource(R.drawable.user_profile),
                                contentDescription = "Profile Icon",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SushiRed)
            )
        },
        bottomBar = {
            // ... (ส่วน BottomBar คงเดิม)
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

