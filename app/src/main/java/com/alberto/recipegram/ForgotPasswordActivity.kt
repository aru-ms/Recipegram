package com.alberto.recipegram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : ComponentActivity() {

    private var etEmail: EditText? = null
    private var btnSend: Button? = null
    // Referencias firebase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        inicializar()
    }

    private fun inicializar() {
        etEmail = findViewById(R.id.etEmailFPass) as EditText
        btnSend = findViewById(R.id.btnSend) as Button
        mAuth = FirebaseAuth.getInstance()
        btnSend!!.setOnClickListener{sendPasswordResetEmail()}
    }

    private fun sendPasswordResetEmail() {
        val email = etEmail?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Se le ha enviado un correo para reestablecer su contraseña", Toast.LENGTH_SHORT).show()
                    goMain()
                } else {
                    Toast.makeText(this, "Este correo no existe en nuestra base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Introduza su correo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}