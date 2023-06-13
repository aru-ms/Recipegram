package com.alberto.recipegram.model

import java.util.*

class Recipe (
    var imageUrl: String,
    var name: String,
    var userId: String,
    var ingredients: String,
    var description: String,
    var timestamp: Date?
    ) {
    constructor() : this("", "", "", "", "", null)
}

