package com.restaurandes.domain.model

data class Review(
    val id: String,
    val restaurantId: String,
    val userId: String,
    val userName: String,
    val rating: Double,
    val comment: String,
    val timestamp: Long,
    val imageUrls: List<String> = emptyList()
)
