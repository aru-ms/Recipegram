package com.alberto.recipegram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.alberto.recipegram.ui.theme.RSRecetasTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            RSRecetasTheme {
                LoginScreen()
            }
        }
    }

    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize()
                .background(colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.padding(16.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = { loginUser(context, email, password) },
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Login")
            }
            TextButton(
                onClick = { signUp(context) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Sign Up")
            }
            TextButton(
                onClick = { forgotPassword(context) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Forgot Password")
            }
        }
    }

    private fun loginUser(context: Context, email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user?.isEmailVerified == true) {
                        goHome(context)
                    } else {
                        Toast.makeText(context, "Necesitas verificar tu correo", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "El correo o la contraseña no son correctos", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun goHome(context: Context) {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun forgotPassword(context: Context) {
        val intent = Intent(context, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun signUp(context: Context) {
        val intent = Intent(context, SignUpActivity::class.java)
        startActivity(intent)
    }
}
