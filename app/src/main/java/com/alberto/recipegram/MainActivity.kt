package com.alberto.recipegram

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alberto.recipegram.ui.theme.RSRecetasTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.properties.Delegates

class MainActivity : ComponentActivity() {

    private val TAG = "LoginActivity"
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var mProgressBar: ProgressBar

    // Creamos nuestra variable de autenticación firebase
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContent {
            RSRecetasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        } */
        setContentView(R.layout.activity_main)
        inicializar()
    }

    private fun inicializar() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        mProgressBar = ProgressBar(this)
        mAuth = FirebaseAuth.getInstance()
    }

    private fun loginUser() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar.visibility = View.VISIBLE
                // Iniciamos sesión en FireBase
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user!!.isEmailVerified) {
                            goHome()
                        } else {
                            Toast.makeText(this,"Necesitas verificar tu correo", Toast.LENGTH_SHORT).show()
                        }
                    /*task ->
                        // Si el login es correcto, vamos a la pantalla principal
                        if (task.isSuccessful) {
                            goHome()
                        } else {
                            // Si no, avisamos al usuario de que el login es incorrecto
                            Toast.makeText(
                                this,
                                "El correo o la contraseña no son correctos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "El correo o la contraseña están vacíos", Toast.LENGTH_SHORT)
                    .show()
            */}.addOnFailureListener(this) {
                        Toast.makeText(this, "El correo o la contraseña no son correctos", Toast.LENGTH_SHORT).show()
                    }}
    }

    private fun goHome() {
        mProgressBar.visibility = View.INVISIBLE
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun login(view: View) {
        loginUser()
    }

    fun forgotPassword(view: View) {
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    fun signUp(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
    }
}