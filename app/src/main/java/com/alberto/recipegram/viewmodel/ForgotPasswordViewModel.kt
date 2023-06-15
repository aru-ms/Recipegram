package com.alberto.recipegram.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _resetEmailSent = MutableLiveData<Boolean>()
    val resetEmailSent: LiveData<Boolean> = _resetEmailSent

    fun sendPasswordResetEmail(email: String) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _resetEmailSent.value = true
            } else {
                _resetEmailSent.value = false
            }
        }
    }
}