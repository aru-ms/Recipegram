package com.alberto.recipegram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alberto.recipegram.model.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileViewModel : ViewModel() {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firestore = FirebaseFirestore.getInstance()

    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: MutableLiveData<String?>
        get() = _profileImageUrl

    private val _userEmail = MutableLiveData<String>()
    private val _username = MutableLiveData<String?>()
    val userEmail: LiveData<String>
        get() = _userEmail

    val userUsername: MutableLiveData<String?>
        get() = _username

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        currentUser?.let { user ->
            // Load user profile image
            val userId = user.uid
            val usersCollection = firestore.collection("users")
            val userDocument = usersCollection.document(userId)

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                val photoUrl = documentSnapshot.getString("photoUrl")
                _profileImageUrl.value = photoUrl
            }

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                val username = documentSnapshot.getString("username")
                _username.value = username
            }

            // Load user email
            _userEmail.value = user.email

            // Load user's uploaded recipes
            val recipesQuery = firestore.collection("recipes")
                .whereEqualTo("userId", user.uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)

            recipesQuery.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.let { documents ->
                    val recipeList = mutableListOf<Recipe>()
                    for (document in documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        recipeList.add(recipe)
                    }
                    _recipes.value = recipeList
                }
            }
        }
    }
}