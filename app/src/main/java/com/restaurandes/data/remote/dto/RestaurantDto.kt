package com.restaurandes.data.remote.dto

import com.restaurandes.domain.model.Restaurant

data class RestaurantDto(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val priceRange: String,
    val rating: Double,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String,
    val openingHours: String,
    val isOpen: Boolean,
    val lastUpdated: Long,
    val tags: List<String> = emptyList(),
    val reviewCount: Int = 0
)

fun RestaurantDto.toDomain(): Restaurant {
    return Restaurant(
        id = id,
        name = name,
        description = description,
        category = category,
        priceRange = priceRange,
        rating = rating,
        imageUrl = imageUrl,
        latitude = latitude,
        longitude = longitude,
        address = address,
        phone = phone,
        openingHours = openingHours,
        isOpen = isOpen,
        lastUpdated = lastUpdated,
        tags = tags,
        reviewCount = reviewCount
    )
}
