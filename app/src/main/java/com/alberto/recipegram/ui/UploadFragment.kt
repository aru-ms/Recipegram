package com.alberto.recipegram.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alberto.recipegram.R
import com.alberto.recipegram.viewmodel.UploadViewModel

class UploadFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val CAMERA_PERMISSION_REQUEST_CODE = 3

    private lateinit var recipeImageView: ImageView
    private lateinit var galleryButton: Button
    private lateinit var cameraButton: Button
    private lateinit var recipeNameEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var uploadButton: Button

    private var recipeImageBitmap: Bitmap? = null

    private lateinit var uploadViewModel: UploadViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        recipeImageView = view.findViewById(R.id.recipeImageView)
        galleryButton = view.findViewById(R.id.galleryButton)
        cameraButton = view.findViewById(R.id.cameraButton)
        recipeNameEditText = view.findViewById(R.id.recipeNameEditText)
        ingredientsEditText = view.findViewById(R.id.ingredientsEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        uploadButton = view.findViewById(R.id.uploadButton)

        uploadViewModel = ViewModelProvider(this)[UploadViewModel::class.java]

        galleryButton.setOnClickListener {
            openGallery()
        }

        cameraButton.setOnClickListener {
            checkCameraPermission()
        }

        uploadButton.setOnClickListener {
            uploadRecipe()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    data?.data?.let { uri ->
                        recipeImageBitmap =
                            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                        recipeImageView.setImageBitmap(recipeImageBitmap)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    recipeImageBitmap = imageBitmap
                    recipeImageView.setImageBitmap(recipeImageBitmap)
                }
            }
        }
    }

    private fun uploadRecipe() {
        val recipeName = recipeNameEditText.text.toString().trim()
        val ingredients = ingredientsEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        uploadViewModel.uploadRecipe(recipeName, ingredients, description, recipeImageBitmap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Log.d("RecipeUploadFragment", "Camera permission denied")
            }
        }
    }
}