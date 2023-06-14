package com.alberto.recipegram

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alberto.recipegram.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SearchFragment : Fragment() {
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val recipeList: MutableList<Recipe> = mutableListOf()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)

        setupRecyclerView()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                searchRecipes(keyword)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        // Use your existing RecipeAdapter and set it to the recyclerView
        recipeAdapter = RecipeAdapter(recipeList, false)
        val layoutManager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recipeAdapter
    }

    private fun searchRecipes(keyword: String) {
        searchJob?.cancel() // Cancel the previous search job if exists

        if (keyword.isNotEmpty()) {
            searchJob = GlobalScope.launch(Dispatchers.Main) {
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
            recipeList.clear()
            recipeAdapter.notifyDataSetChanged()
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
        recipeList.clear()
        recipeList.addAll(recipes)
        recipeAdapter.notifyDataSetChanged()
    }
}
