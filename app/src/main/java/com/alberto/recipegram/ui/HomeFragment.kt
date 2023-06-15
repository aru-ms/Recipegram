package com.alberto.recipegram.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alberto.recipegram.R
import com.alberto.recipegram.viewmodel.HomeViewModel
import com.alberto.recipegram.viewmodel.RecipeAdapter


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipes_home)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeAdapter = RecipeAdapter(emptyList(), false)
        recyclerView.adapter = recipeAdapter

        // Initialize HomeViewModel
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Observe the recipes LiveData and update the adapter when it changes
        homeViewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            recipeAdapter.setRecipes(recipes)
        })

        // Fetch recipe data from Firestore
        homeViewModel.fetchRecipes()

        return view
    }
}