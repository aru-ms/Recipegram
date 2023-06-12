package com.alberto.recipegram

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
import com.squareup.picasso.Picasso
import com.alberto.recipegram.model.User

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var emailTextView: TextView
    private lateinit var recipesRecyclerView: RecyclerView
    private lateinit var recipesAdapter: RecipesAdapter

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImageView = view.findViewById(R.id.profileImageView)
        emailTextView = view.findViewById(R.id.emailTextView)
        recipesRecyclerView = view.findViewById(R.id.recipesRecyclerView)
        val fotoUrl = firestore.collection("users").document(currentUser!!.uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    val photoUrl = user?.photoUrl
                    // Use the photoUrl as needed
                    Log.d(TAG, "Photo URL: $photoUrl")
                } else {
                    Log.d(TAG, "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to retrieve user document", e)
            }

        recipesAdapter = RecipesAdapter()
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
                if (documentSnapshot.exists()) {
                    val photoUrl = documentSnapshot.getString("photoUrl")
                    if (!photoUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(photoUrl)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(profileImageView)
                    } else {
                        // Default image or error handling
                    }
                } else {
                    // Document does not exist
                }
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

    private inner class RecipesAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
        private var recipes: List<Recipe> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
            return RecipeViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            val recipe = recipes[position]
            holder.bind(recipe)
        }

        override fun getItemCount(): Int {
            return recipes.size
        }

        fun setRecipes(recipeList: List<Recipe>) {
            recipes = recipeList
            notifyDataSetChanged()
        }
    }

    private inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)

        fun bind(recipe: Recipe) {
            Picasso.get().load(recipe.photoUrl).into(recipeImageView)
        }
    }
}