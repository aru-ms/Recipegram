package com.alberto.recipegram.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginUser(context: Context, email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit, onEmailNotVerified: () -> Unit) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user?.isEmailVerified == true) {
                        onSuccess()
                    } else {
                        onEmailNotVerified()
                    }
                }
                .addOnFailureListener {
                    onFailure()
                }
        }
    }
}