package com.alberto.recipegram.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {
    private val _firstName = mutableStateOf("")
    val firstName: State<String> = _firstName

    private val _lastName = mutableStateOf("")
    val lastName: State<String> = _lastName

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _progressBarVisible = mutableStateOf(false)
    val progressBarVisible: State<Boolean> = _progressBarVisible

    fun setFirstName(value: String) {
        _firstName.value = value
    }

    fun setLastName(value: String) {
        _lastName.value = value
    }

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun createNewAccount() {
        val firstName = _firstName.value
        val lastName = _lastName.value
        val email = _email.value
        val password = _password.value
        val confirmPassword = _confirmPassword.value

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
            _progressBarVisible.value = true

            auth.signOut()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(getApplication(), "Verify your email with the received link", Toast.LENGTH_SHORT).show()
                                goMain()
                            } else {
                                Toast.makeText(getApplication(), "Error verifying email", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(getApplication(), "Error creating user", Toast.LENGTH_SHORT).show()
                    }
                    _progressBarVisible.value = false
                }
        } else {
            Toast.makeText(getApplication(), "Fill in all the fields or the passwords do not match", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goMain() {
        val intent = Intent(getApplication(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        getApplication<Application>().startActivity(intent)
    }
}