package com.restaurandes.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: String? = null,
    val favoriteRestaurants: List<String> = emptyList(),
    val dietaryPreferences: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
