package com.restaurandes.domain.model

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val priceRange: String, // "$", "$$", "$$$"
    val rating: Double,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String,
    val openingHours: String,
    val isOpen: Boolean,
    val lastUpdated: Long,
    val tags: List<String> = emptyList(), // vegetarian, vegan, gluten-free, etc.
    val reviewCount: Int = 0
)
