package com.alberto.recipegram.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class SignUpViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _signUpSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val signUpSuccess: LiveData<Boolean>
        get() = _signUpSuccess

    fun signUp(email: String, password: String, confirmPass: String, profileImageBitmap: Bitmap?) {
        if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            _signUpSuccess.value = false
            return
        }

        if (profileImageBitmap == null) {
            _signUpSuccess.value = false
            return
        }

        if (confirmPass != password) {
            _signUpSuccess.value = false
            return
        }

        // Convert the profile image to a byte array
        val baos = ByteArrayOutputStream()
        profileImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val profileImageBytes = baos.toByteArray()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid ?: ""

                    val userDocRef = firestore.collection("users").document(userId)

                    val userObject = hashMapOf<String, Any>(
                        "email" to email,
                        "photoURL" to "" // Initialize the photoUrl to an empty string
                    )
                    // Add more fields as needed (e.g., username)

                    userDocRef.set(userObject)
                        .addOnSuccessListener {
                            saveProfileImageToFirestore(userId, profileImageBytes) { photoUrl ->
                                if (photoUrl != null) {
                                    userDocRef.update("photoURL", photoUrl)
                                        .addOnSuccessListener {
                                            sendEmailVerification(user)
                                        }
                                        .addOnFailureListener { e ->
                                            _signUpSuccess.value = false
                                        }
                                } else {
                                    _signUpSuccess.value = false
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            _signUpSuccess.value = false
                        }
                } else {
                    _signUpSuccess.value = false
                }
            }
    }

    private fun saveProfileImageToFirestore(userId: String, imageBytes: ByteArray, completion: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val profileImageRef = storageRef.child("profile_images/$userId.jpg")

        profileImageRef.putBytes(imageBytes)
            .addOnSuccessListener { _ ->
                profileImageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        val userRef = firestore.collection("users").document(userId)
                        userRef.update("photoUrl", photoUrl)
                            .addOnSuccessListener {
                                completion(photoUrl)
                            }
                            .addOnFailureListener { e ->
                                completion(null)
                            }
                    }
                    .addOnFailureListener { e ->
                        completion(null)
                    }
            }
            .addOnFailureListener { e ->
                completion(null)
            }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnSuccessListener {
                _signUpSuccess.value = true
            }
            ?.addOnFailureListener { e ->
                _signUpSuccess.value = false
            }
    }
}