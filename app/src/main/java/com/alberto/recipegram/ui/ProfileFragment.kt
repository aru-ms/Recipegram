package com.alberto.recipegram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alberto.recipegram.model.Recipe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var emailTextView: TextView
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipesAdapter: RecipeAdapter

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firestore = FirebaseFirestore.getInstance()
    private val recipeList = mutableListOf<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        emailTextView = view.findViewById(R.id.emailTextView)
        recipesRecyclerView = view.findViewById(R.id.recipes_profile)

        recipesAdapter = RecipeAdapter(recipeList, true)
        recipesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recipesRecyclerView.adapter = recipesAdapter

        loadProfileData()

        return view
    }

    private fun loadProfileData() {
        currentUser?.let { user ->
            // Load user profile image
            val userId = user.uid
            val usersCollection = firestore.collection("users")
            val userDocument = usersCollection.document(userId)

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                val photoUrl = documentSnapshot.getString("photoUrl")
                Glide.with(this)
                    .load(photoUrl)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(profileImageView)
            }

            // Load user email
            emailTextView.text = user.email

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
                    recipesAdapter.setRecipes(recipeList)
                }
            }
        }
    }
}