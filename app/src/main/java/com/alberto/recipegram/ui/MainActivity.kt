package com.alberto.recipegram.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.alberto.recipegram.R
import com.alberto.recipegram.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(R.layout.activity_main)
        setupViews()
    }

    private fun setupViews() {
        val context: Context = this
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val forgotPasswordTextView: TextView = findViewById(R.id.forgotPasswordTextView)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            mainViewModel.loginUser(
                context,
                email,
                password,
                onSuccess = {
                    goHome(context)
                },
                onFailure = {
                    Toast.makeText(context, "El correo o la contraseña no son correctos", Toast.LENGTH_SHORT).show()
                },
                onEmailNotVerified = {
                    Toast.makeText(context, "Necesitas verificar tu correo", Toast.LENGTH_SHORT).show()
                }
            )
        }

        signUpButton.setOnClickListener {
            signUp(context)
        }

        forgotPasswordTextView.setOnClickListener {
            forgotPassword(context)
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