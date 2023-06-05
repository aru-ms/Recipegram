package com.alberto.recipegram

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class UploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UploadRecipeScreen()
        }
    }
}

@Composable
fun UploadRecipeScreen() {
    val context = LocalContext.current
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    val recipeNameState = remember { mutableStateOf("") }
    val recipeDescriptionState = remember { mutableStateOf("") }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        val uri = Uri.fromFile(File.createTempFile("temp", null, context.cacheDir))
        val outputStream = context.contentResolver.openOutputStream(uri)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.close()
        imageUriState.value = uri
    }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Handle the selected image URI here
        imageUriState.value = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUriState.value != null) {
            val bitmap = loadImageBitmap(imageUriState.value!!)
            Image(
                bitmap = bitmap!!,
                contentDescription = "Recipe Photo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "Recipe Photo",
                modifier = Modifier.size(200.dp)
            )
        }

        Button(
            onClick = {
                // Open the camera to take a picture
                takePicture.launch()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Take Photo")
        }

        Button(
            onClick = {
                // Open the gallery to pick an image
                pickImage.launch("image/*")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Choose from Gallery")
        }

        TextField(
            value = recipeNameState.value,
            onValueChange = { recipeNameState.value = it },
            label = { Text("Recipe Name") },
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = recipeDescriptionState.value,
            onValueChange = { recipeDescriptionState.value = it },
            label = { Text("Recipe Description") },
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                // Upload the recipe with the photo
                val recipeName = recipeNameState.value
                val recipeDescription = recipeDescriptionState.value
                val imageUri = imageUriState.value

                // Perform the necessary actions with the recipe data
                // For example, upload the photo to Firebase Storage and save the recipe details to a database

                // Reset the states
                imageUriState.value = null
                recipeNameState.value = ""
                recipeDescriptionState.value = ""
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Upload Recipe")
        }
    }
}

@Composable
fun loadImageBitmap(uri: Uri): ImageBitmap? {
    val bitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    val context = LocalContext.current
    LaunchedEffect(uri) {
        val bitmapResult = withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
        bitmap.value = bitmapResult?.asImageBitmap()
    }

    Image(
        bitmap = bitmap.value!!,
        contentDescription = "Recipe Photo",
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
    )

    return bitmap.value
}