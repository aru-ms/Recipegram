package com.alberto.recipegram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alberto.recipegram.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    fun fetchRecipes() {
        firestore.collection("recipes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = mutableListOf<Recipe>()
                for (document in querySnapshot) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipes.add(recipe)
                }
                _recipes.value = recipes
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}