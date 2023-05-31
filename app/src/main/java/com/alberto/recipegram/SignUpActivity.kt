package com.alberto.recipegram

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alberto.recipegram.ui.theme.RSRecetasTheme
import com.alberto.recipegram.viewmodel.SignUpViewModel
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : ComponentActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val progressBarVisible by viewModel.progressBarVisible.observeAsState(false)

            val firstName by viewModel.firstName.observeAsState("")
            val lastName by viewModel.lastName.observeAsState("")
            val email by viewModel.email.observeAsState("")
            val password by viewModel.password.observeAsState("")
            val confirmPassword by viewModel.confirmPassword.observeAsState("")

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = firstName,
                    onValueChange = { viewModel.setFirstName(it) },
                    label = { Text("First Name") }
                )
                TextField(
                    value = lastName,
                    onValueChange = { viewModel.setLastName(it) },
                    label = { Text("Last Name") }
                )
                TextField(
                    value = email,
                    onValueChange = { viewModel.setEmail(it) },
                    label = { Text("Email") }
                )
                TextField(
                    value = password,
                    onValueChange = { viewModel.setPassword(it) },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                TextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.setConfirmPassword(it) },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(
                    onClick = { viewModel.createNewAccount() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Sign Up")
                }
                if (progressBarVisible) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}