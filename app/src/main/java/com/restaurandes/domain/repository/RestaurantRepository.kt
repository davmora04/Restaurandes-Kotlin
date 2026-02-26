package com.restaurandes.domain.repository

import com.restaurandes.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    suspend fun getRestaurants(): Result<List<Restaurant>>
    suspend fun getRestaurantById(id: String): Result<Restaurant>
    suspend fun searchRestaurants(query: String): Result<List<Restaurant>>
    suspend fun getRestaurantsByCategory(category: String): Result<List<Restaurant>>
    suspend fun getNearbyRestaurants(latitude: Double, longitude: Double, radiusKm: Double): Result<List<Restaurant>>
    fun observeRestaurants(): Flow<List<Restaurant>>
}
