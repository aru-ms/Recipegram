package com.alberto.recipegram

data class Recipe(
    var title: String?= "",
    var description: String? = "",
    var ingredients: String? = "",
    var instructions: String? = "",
    var photoUrl: String? = ""
)
