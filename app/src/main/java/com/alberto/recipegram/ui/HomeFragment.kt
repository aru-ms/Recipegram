package com.alberto.recipegram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alberto.recipegram.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipes_home)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeAdapter = RecipeAdapter(emptyList(), false)
        recyclerView.adapter = recipeAdapter

        // Fetch recipe data from Firestore
        fetchRecipes()

        return view
    }

    private fun fetchRecipes() {
        firestore.collection("recipes")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = mutableListOf<Recipe>()
                for (document in querySnapshot) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipes.add(recipe)
                }
                recipeAdapter.setRecipes(recipes)
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}