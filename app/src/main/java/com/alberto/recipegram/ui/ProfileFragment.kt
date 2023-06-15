package com.alberto.recipegram.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alberto.recipegram.R
import com.alberto.recipegram.viewmodel.ProfileViewModel
import com.alberto.recipegram.viewmodel.RecipeAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipesAdapter: RecipeAdapter

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        usernameTextView = view.findViewById(R.id.emailTextView)
        recipesRecyclerView = view.findViewById(R.id.recipes_profile)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        setupRecyclerView()

        profileViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .transform(CircleCrop())
                .into(profileImageView)
        }

        profileViewModel.userUsername.observe(viewLifecycleOwner) { username ->
            usernameTextView.text = username
        }

        profileViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipesAdapter.setRecipes(recipes)
        }

        return view
    }

    private fun setupRecyclerView() {
        recipesAdapter = RecipeAdapter(emptyList(), true)
        recipesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recipesRecyclerView.adapter = recipesAdapter
    }
}