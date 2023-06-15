package com.alberto.recipegram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alberto.recipegram.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val recipeList: MutableLiveData<List<Recipe>> = MutableLiveData()

    private var searchJob: Job? = null

    val recipes: LiveData<List<Recipe>>
        get() = recipeList

    fun searchRecipes(keyword: String) {
        searchJob?.cancel() // Cancel the previous search job if exists

        if (keyword.isNotEmpty()) {
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                try {
                    val querySnapshot = getRecipesFromFirestore(keyword)
                    val recipes = querySnapshot.toObjects(Recipe::class.java)
                    updateRecipeList(recipes)
                } catch (e: Exception) {
                    // Handle any exceptions that occurred during the search
                }
            }
        } else {
            // Clear the recipe list if the search keyword is empty
            recipeList.value = emptyList()
        }
    }

    private suspend fun getRecipesFromFirestore(keyword: String): QuerySnapshot {
        return firestore.collection("recipes")
            .orderBy("name") // Adjust this based on your Firestore collection structure
            .startAt(keyword)
            .endAt(keyword + "\uf8ff")
            .get()
            .await()
    }

    private fun updateRecipeList(recipes: List<Recipe>) {
        recipeList.value = recipes
    }
}