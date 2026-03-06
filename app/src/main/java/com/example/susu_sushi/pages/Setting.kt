package com.example.susu_sushi.pages

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.susu_sushi.components.BackPageButton
import com.example.susu_sushi.data.viewModel.AuthViewModel
import com.example.susu_sushi.ui.theme.SushiRed
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

@Composable
fun SettingScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val currentUser = authViewModel.currentUser
    val userId = currentUser?.uid

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var PasswordNotMatch by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }  // ✅ ปิด remember ให้ถูกต้อง

    // ✅ ย้ายมาอยู่ระดับ top-level ของ Composable
    val profileImageUrl by authViewModel.profileImageUrl.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        if (userId == null) {
            Toast.makeText(context, "กรุณาเข้าสู่ระบบก่อน", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        isUploading = true
        val storageRef = FirebaseStorage.getInstance().reference.child("users/profile/$userId.jpg")

        // ✅ แปลงเป็น ByteArray ทันที ก่อนที่ permission จะหมด
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes == null) {
                isUploading = false
                Toast.makeText(context, "ไม่สามารถอ่านไฟล์ได้", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            // ✅ ใช้ putBytes แทน putFile
            storageRef.putBytes(bytes)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val url = downloadUri.toString()
                        FirebaseFirestore.getInstance()
                            .collection("user_datas")
                            .document(userId)
                            .set(mapOf("image_url" to url), SetOptions.merge())
                            .addOnSuccessListener {
                                isUploading = false
                                Toast.makeText(context, "อัปโหลดสำเร็จ!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                isUploading = false
                                Toast.makeText(context, "Firestore Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }.addOnFailureListener { e ->
                        isUploading = false
                        Toast.makeText(context, "URL Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    isUploading = false
                    Toast.makeText(context, "Upload Fail: ${e.message}", Toast.LENGTH_LONG).show()
                }

        } catch (e: Exception) {
            isUploading = false
            Toast.makeText(context, "อ่านไฟล์ไม่ได้: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackPageButton(onBack = { navController.popBackStack() })

            Button(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("category")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
            ) {
                Text(text = "logout", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Setting",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image Section
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable(enabled = !isUploading) { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUrl != null) {
                    Log.d("SettingScreen", "Profile Image URL: $profileImageUrl")
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(profileImageUrl)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build(),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black))
                }
                
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.wrapContentSize().shadow(4.dp, RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(20.dp),
                enabled = !isUploading
            ) {
                Text(text = if (isUploading) "Uploading..." else "Upload Profile", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(32.dp))

            // Password Section
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                color = Color.White
            ) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = Color.LightGray) },
                    modifier = Modifier.fillMaxSize(),
                    visualTransformation = PasswordVisualTransformation() ,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                color = Color.White
            ) {
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("ConfirmPassword", color = Color.LightGray) },
                    modifier = Modifier.fillMaxSize(),
                    visualTransformation = PasswordVisualTransformation() ,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            if (PasswordNotMatch) {
                Text(text = "password not match", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (password.isNotEmpty() && password == confirmPassword){
                        authViewModel.changePassword(password)
                        navController.navigate("category")
                    } else {
                        PasswordNotMatch = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(6.dp, RoundedCornerShape(50.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = SushiRed),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(text = "reset password", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
