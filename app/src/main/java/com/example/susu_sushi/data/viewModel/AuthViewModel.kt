package com.example.susu_sushi.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        object ResetPasswordSent : AuthState()
        data class Error(val message: String) : AuthState()
        data class LoggedIn(val user: FirebaseUser) : AuthState()  // ← keep only this
    }

    //------------------ ลงทะเบียนใช้งาน ------------------
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "เกิดข้อผิดพลาด")
            }
        }
    }

    //------------------ รีเซตรหัสผ่าน ------------------
    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.currentUser?.updatePassword(newPassword)?.await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "เกิดข้อผิดพลาด")
            }
        }
    }


    //------------------ ล็อกอินด้วยอีเมล์ ------------------
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.LoggedIn(auth.currentUser!!)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "เกิดข้อผิดพลาด")
            }
        }
    }

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl.asStateFlow()

    private var profileListener: ListenerRegistration? = null

    fun startListeningProfile(userId: String) {
        profileListener?.remove() // remove listener เก่าก่อน
        profileListener = FirebaseFirestore.getInstance()
            .collection("user_datas")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                Log.d("AuthViewModel", "Snapshot received: ${snapshot?.data}")
                _profileImageUrl.value = snapshot?.getString("image_url")
            }
    }

    fun stopListeningProfile() {
        profileListener?.remove()
        _profileImageUrl.value = null
    }

    //------------------ ออกจากระบบ ------------------
    fun logout() {
        stopListeningProfile() // ✅ เพิ่มบรรทัดนี้
        auth.signOut()
        _authState.value = AuthState.Idle
    }


    fun resetState() {
        _authState.value = AuthState.Idle
    }

}