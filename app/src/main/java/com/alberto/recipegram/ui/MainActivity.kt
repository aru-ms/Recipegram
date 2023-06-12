package com.alberto.recipegram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main)
        setupViews()
    }

    private fun setupViews() {
        val context: Context = this
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signUpButton: Button = findViewById(R.id.signUpButton)
        val forgotPasswordButton: Button = findViewById(R.id.forgotPasswordButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            loginUser(context, email, password)
        }

        signUpButton.setOnClickListener {
            signUp(context)
        }

        forgotPasswordButton.setOnClickListener {
            forgotPassword(context)
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
