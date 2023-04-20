package com.alberto.recipegram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.properties.Delegates

class SignUpActivity : ComponentActivity() {

    private lateinit var txtName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private var firstName by Delegates.notNull<String>()
    private var lastName by Delegates.notNull<String>()
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        inicializar()
    }

    private fun inicializar() {
        txtName = findViewById(R.id.txtName)
        txtLastName = findViewById(R.id.txtLastName)
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        progressBar = ProgressBar(this)

        // Creamos una instancia de la base de datos para almacenar los datos del usuario

        database = FirebaseDatabase.getInstance()

        // Creamos una instancia para la autenticación de los usuarios

        auth = FirebaseAuth.getInstance()

        databaseReference = database.reference.child("Usuarios")
    }

    private fun createNewAccount() {
        firstName = txtName.text.toString()
        lastName = txtLastName.text.toString()
        email = txtEmail.text.toString()
        password = txtPassword.text.toString()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                // Obtenemos la ID del usuario que se está registrando
                val user:FirebaseUser = auth.currentUser!!
                // Enviamos mail de verificación al usuario
                verifyEmail(user)
                // Damos de alta al usuario
                val currentUserDb = databaseReference.child(user.uid)
                // Agregamos el nombre completo a la ID del usuario
                currentUserDb.child("nombre").setValue(firstName)
                currentUserDb.child("apellido").setValue(lastName)
                // Accedemos a la vista principal
                goHomePage()
            }.addOnFailureListener{
                // Si el registro falla enviamos un mensaje al usuario
                Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Avisamos al usuario de que hay campos vacíos
            Toast.makeText(this, "Hay campos vacíos en el registro", Toast.LENGTH_SHORT).show()
        }
    }

    fun signUp(view: View) {
        createNewAccount()
    }

    private fun goHomePage() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        // Ocultamos la progressBar
        progressBar.visibility = View.INVISIBLE
    }

    private fun verifyEmail(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener(this) {
            // Verificamos que la tarea se realizó correctamente
            task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Email " + user.email, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al verificar el correo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}