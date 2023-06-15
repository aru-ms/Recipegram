package com.alberto.recipegram.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.alberto.recipegram.R
import com.alberto.recipegram.viewmodel.ForgotPasswordViewModel

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnSend: Button
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        initialize()
        observeResetEmailSent()
    }

    private fun initialize() {
        etEmail = findViewById(R.id.etEmailFPass)
        btnSend = findViewById(R.id.btnSend)
        btnSend.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {
        val email = etEmail.text.toString()
        if (email.isNotEmpty()) {
            viewModel.sendPasswordResetEmail(email)
        } else {
            Toast.makeText(this, "Introduza su correo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeResetEmailSent() {
        viewModel.resetEmailSent.observe(this) { resetEmailSent ->
            if (resetEmailSent) {
                Toast.makeText(
                    this,
                    "Se le ha enviado un correo para restablecer su contraseña",
                    Toast.LENGTH_SHORT
                ).show()
                goMain()
            } else {
                Toast.makeText(
                    this,
                    "Este correo no existe en nuestra base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}