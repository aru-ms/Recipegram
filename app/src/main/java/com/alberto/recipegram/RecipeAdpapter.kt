package com.alberto.recipegram

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alberto.recipegram.model.Recipe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore

class RecipeAdapter(private var recipeList: List<Recipe>, private val isProfileFragment: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HOME = 1
    private val VIEW_TYPE_PROFILE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HOME) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_home, parent, false)
            HomeViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_profile, parent, false)
            ProfileViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val recipe = recipeList[position]
        when (holder) {
            is HomeViewHolder -> holder.bind(recipe)
            is ProfileViewHolder -> holder.bind(recipe)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isProfileFragment) {
            VIEW_TYPE_PROFILE
        } else {
            VIEW_TYPE_HOME
        }
    }

    fun setRecipes(recipes: List<Recipe>) {
        recipeList = recipes
        notifyDataSetChanged()
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val ingredientsTextView: TextView = itemView.findViewById(R.id.ingredientsTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val userTextView: TextView = itemView.findViewById(R.id.userTextView)
        private var profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)

        fun bind(recipe: Recipe) {

            val context = itemView.context
            val userId = recipe.userId
            // Load recipe image using Glide
            Glide.with(itemView)
                .load(recipe.imageUrl)
                .into(recipeImageView)

            titleTextView.text = recipe.name
            ingredientsTextView.text = recipe.ingredients
            descriptionTextView.text = recipe.description

            // Retrieve the user email using the userId from Firestore
            retrieveUserEmail(userId) { userEmail ->
                userTextView.text = userEmail
            }

            val usersCollection = FirebaseFirestore.getInstance().collection("users")
            val userDocument = usersCollection.document(userId)

            userDocument.get().addOnSuccessListener { documentSnapshot ->
                val photoUrl = documentSnapshot.getString("photoUrl")
                Glide.with(context)
                    .load(photoUrl)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(profileImageView)
            }
        }

        private fun retrieveUserEmail(userId: String, callback: (String) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userEmail = document.getString("email")
                        if (!userEmail.isNullOrBlank()) {
                            callback(userEmail)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("RecipeAdapter", "Error retrieving user email: $exception")
                }
        }
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipeImageView)

        init {
            // Set click listener on the recipeImageView
            recipeImageView.setOnClickListener { onClick(it) }
        }

        private fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val recipe = recipeList[position]
                showRecipeDetailsDialog(recipe)
            }
        }

        private fun showRecipeDetailsDialog(recipe: Recipe) {
            val dialogBuilder = AlertDialog.Builder(itemView.context)
            dialogBuilder.setTitle(recipe.name)
            dialogBuilder.setMessage("Ingredientes: ${recipe.ingredients}\nPasos de la receta: ${recipe.description}")
            dialogBuilder.setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            dialogBuilder.create().show()
        }

        fun bind(recipe: Recipe) {
            // Load recipe image using Glide
            Glide.with(itemView)
                .load(recipe.imageUrl)
                .into(recipeImageView)
            // Bind other recipe data to your view holder views
        }
    }
}