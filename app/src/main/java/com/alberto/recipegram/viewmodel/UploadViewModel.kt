package com.alberto.recipegram.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class UploadViewModel : ViewModel() {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun uploadRecipe(
        recipeName: String,
        ingredients: String,
        description: String,
        recipeImageBitmap: Bitmap?
    ) {
        if (recipeName.isNotEmpty() && ingredients.isNotEmpty() && description.isNotEmpty() && recipeImageBitmap != null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid

            if (userId != null) {
                // Generate a unique filename for the image
                val filename = UUID.randomUUID().toString() + ".jpg"

                // Reference to the image file in Firebase Storage
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("recipe_images/$filename")

                // Compress the image and upload to Firebase Storage
                val baos = ByteArrayOutputStream()
                recipeImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
                val imageData = baos.toByteArray()
                val uploadTask = storageRef.putBytes(imageData)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result

                        // Create a new recipe document in Firestore
                        val recipeData = hashMapOf(
                            "name" to recipeName,
                            "ingredients" to ingredients,
                            "description" to description,
                            "imageUrl" to downloadUrl.toString(),
                            "userId" to userId,
                            "timestamp" to FieldValue.serverTimestamp() // Add timestamp field
                        )

                        firestore.collection("recipes")
                            .add(recipeData)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    "RecipeUploadViewModel",
                                    "Recipe uploaded successfully! Document ID: ${documentReference.id}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.e("RecipeUploadViewModel", "Recipe upload failed", e)
                            }
                    } else {
                        Log.e("RecipeUploadViewModel", "Failed to retrieve download URL for the image")
                    }
                }
            } else {
                Log.e("RecipeUploadViewModel", "User ID is null")
            }
        } else {
            Log.d(
                "RecipeUploadViewModel",
                "Please fill in all fields and choose a recipe image"
            )
        }
    }
}