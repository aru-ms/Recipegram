package com.alberto.recipegram.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.alberto.recipegram.R
import com.alberto.recipegram.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var confPasswordEditText: EditText
    private lateinit var usernameEditText: EditText

    private lateinit var viewModel: SignUpViewModel

    private var profileImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        profileImageView = findViewById(R.id.profileImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signupButton)
        confPasswordEditText = findViewById(R.id.confPasswordEditText)
        usernameEditText = findViewById(R.id.usernameEditText)

        selectImageButton.setOnClickListener {
            openGallery()
        }

        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPass = confPasswordEditText.text.toString().trim()
            viewModel.signUp(username, email, password, confirmPass, profileImageBitmap)
        }

        viewModel.signUpSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Sign up successful. Verification email sent", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    data?.data?.let { uri ->
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        profileImageView.setImageBitmap(profileImageBitmap)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}