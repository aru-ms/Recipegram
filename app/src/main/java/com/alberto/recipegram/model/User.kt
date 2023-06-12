package com.alberto.recipegram.model

data class User(
    val email: String,
    val photoUrl: String?,
    // Add other fields as needed
) {
    // Add an empty constructor for Firestore deserialization
    constructor() : this("", null)
}