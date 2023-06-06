package com.alberto.recipegram.model

data class Recipe(
    var title: String?= "",
    var description: String? = "",
    var ingredients: String? = "",
    var instructions: String? = "",
    var photoUrl: String? = ""
)
