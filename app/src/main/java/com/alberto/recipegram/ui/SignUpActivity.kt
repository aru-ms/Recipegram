package com.alberto.recipegram

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var profileImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        profileImageView = findViewById(R.id.profileImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signupButton)

        selectImageButton.setOnClickListener {
            openGallery()
        }

        signUpButton.setOnClickListener {
            signUp()
        }
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

    private fun signUp() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        if (profileImageBitmap == null) {
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert the profile image to a byte array
        val baos = ByteArrayOutputStream()
        profileImageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val profileImageBytes = baos.toByteArray()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid ?: ""

                    // Create a new document with the user's ID in the "users" collection
                    val userDocRef = firestore.collection("users").document(userId)

                    // Create a user object with the necessary data
                    val userObject = hashMapOf<String, Any>(
                        "email" to email,
                        "photoURL" to "" // Initialize the photoUrl to an empty string
                    )
                    // Add more fields as needed (e.g., username)

                    // Save the user object to Firestore
                    userDocRef.set(userObject)
                        .addOnSuccessListener {
                            // Save the profile image to Firestore
                            saveProfileImageToFirestore(userId, profileImageBytes) { photoUrl ->
                                if (photoUrl != null) {
                                    // Photo URL saved successfully
                                    userDocRef.update("photoURL", photoUrl)
                                        .addOnSuccessListener {
                                            // Handle success
                                            sendEmailVerification(user)
                                        }
                                        .addOnFailureListener { e ->
                                            // Handle failure
                                            Toast.makeText(this, "Failed to save profile image URL", Toast.LENGTH_SHORT).show()
                                            Log.e(TAG, "Failed to save profile image URL to Firestore", e)
                                        }
                                } else {
                                    // Failed to save the photo URL
                                    Toast.makeText(this, "Failed to save profile image", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Failed to save user data to Firestore", e)
                        }
                } else {
                    Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                }
            }
    }

    private fun saveProfileImageToFirestore(userId: String, imageBytes: ByteArray, completion: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference

        // Create a unique file name for the profile image
        val profileImageRef = storageRef.child("profile_images/$userId.jpg")

        // Upload the image bytes to Firebase Storage
        profileImageRef.putBytes(imageBytes)
            .addOnSuccessListener { _ ->
                // Get the download URL of the uploaded image
                profileImageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()

                        // Save the photoUrl to Firestore
                        val userRef = firestore.collection("users").document(userId)
                        userRef.update("photoUrl", photoUrl)
                            .addOnSuccessListener {
                                Log.d(TAG, "Profile image URL saved to Firestore")
                                completion(photoUrl) // Pass the photoUrl to the completion callback
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to save profile image URL to Firestore", e)
                                completion(null) // Notify the caller with null if there's an error
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to get download URL of profile image", e)
                        completion(null) // Notify the caller with null if there's an error
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to upload profile image to Firebase Storage", e)
                completion(null) // Notify the caller with null if there's an error
            }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Sign up successful. Verification email sent",
                    Toast.LENGTH_SHORT
                ).show()
                // Redirect to the main activity or any other desired screen
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to send verification email",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "Failed to send verification email", e)
            }
    }

    companion object {
        private const val TAG = "SignUpActivity"
        private const val REQUEST_IMAGE_PICK = 1
    }
}