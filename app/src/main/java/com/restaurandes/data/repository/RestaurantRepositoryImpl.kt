package com.restaurandes.data.repository

import com.restaurandes.data.remote.api.RestaurantApi
import com.restaurandes.data.remote.dto.toDomain
import com.restaurandes.domain.model.Restaurant
import com.restaurandes.domain.repository.RestaurantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.*

class RestaurantRepositoryImpl @Inject constructor(
    private val api: RestaurantApi
) : RestaurantRepository {

    // Mock data for development
    private val mockRestaurants = listOf(
        Restaurant(
            id = "1",
            name = "Café del Campus",
            description = "Café tradicional con opciones de desayuno y almuerzo",
            category = "Café",
            priceRange = "$$",
            rating = 4.5,
            imageUrl = "https://picsum.photos/400/300?random=1",
            latitude = 4.6017,
            longitude = -74.0659,
            address = "Edificio ML, Piso 1",
            phone = "+57 1 3394949",
            openingHours = "7:00 AM - 6:00 PM",
            isOpen = true,
            lastUpdated = System.currentTimeMillis(),
            tags = listOf("vegetarian", "coffee", "fast"),
            reviewCount = 45
        ),
        Restaurant(
            id = "2",
            name = "Food Court RGA",
            description = "Variedad de comidas rápidas y menú del día",
            category = "Comida Rápida",
            priceRange = "$",
            rating = 4.0,
            imageUrl = "https://picsum.photos/400/300?random=2",
            latitude = 4.6023,
            longitude = -74.0652,
            address = "Edificio RGA, Piso 2",
            phone = "+57 1 3394950",
            openingHours = "11:00 AM - 3:00 PM",
            isOpen = true,
            lastUpdated = System.currentTimeMillis(),
            tags = listOf("fast", "economic"),
            reviewCount = 32
        ),
        Restaurant(
            id = "3",
            name = "Restaurante Santo Domingo",
            description = "Comida internacional con ambiente tranquilo",
            category = "Internacional",
            priceRange = "$$$",
            rating = 4.7,
            imageUrl = "https://picsum.photos/400/300?random=3",
            latitude = 4.6012,
            longitude = -74.0665,
            address = "Edificio SD, Piso 3",
            phone = "+57 1 3394951",
            openingHours = "12:00 PM - 8:00 PM",
            isOpen = true,
            lastUpdated = System.currentTimeMillis(),
            tags = listOf("vegetarian", "vegan", "quiet", "formal"),
            reviewCount = 28
        ),
        Restaurant(
            id = "4",
            name = "Juice & Bowls",
            description = "Opciones saludables, jugos naturales y bowls",
            category = "Saludable",
            priceRange = "$$",
            rating = 4.6,
            imageUrl = "https://picsum.photos/400/300?random=4",
            latitude = 4.6020,
            longitude = -74.0655,
            address = "Bloque W, Piso 1",
            phone = "+57 1 3394952",
            openingHours = "7:30 AM - 5:00 PM",
            isOpen = true,
            lastUpdated = System.currentTimeMillis(),
            tags = listOf("healthy", "vegetarian", "vegan", "gluten-free"),
            reviewCount = 51
        ),
        Restaurant(
            id = "5",
            name = "Pizza Campus",
            description = "Pizzas artesanales al horno de leña",
            category = "Italiana",
            priceRange = "$$",
            rating = 4.3,
            imageUrl = "https://picsum.photos/400/300?random=5",
            latitude = 4.6015,
            longitude = -74.0668,
            address = "Cerca Portería Carrera 1",
            phone = "+57 1 3394953",
            openingHours = "11:30 AM - 9:00 PM",
            isOpen = false,
            lastUpdated = System.currentTimeMillis(),
            tags = listOf("vegetarian"),
            reviewCount = 19
        )
    )

    override suspend fun getRestaurants(): Result<List<Restaurant>> {
        return try {
            // TODO: Replace with real API call
            // val response = api.getRestaurants()
            // if (response.isSuccessful && response.body() != null) {
            //     Result.success(response.body()!!.map { it.toDomain() })
            // } else {
            //     Result.failure(Exception("Error loading restaurants"))
            // }
            
            Result.success(mockRestaurants)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRestaurantById(id: String): Result<Restaurant> {
        return try {
            val restaurant = mockRestaurants.find { it.id == id }
            if (restaurant != null) {
                Result.success(restaurant)
            } else {
                Result.failure(Exception("Restaurant not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRestaurants(query: String): Result<List<Restaurant>> {
        return try {
            val filtered = mockRestaurants.filter { restaurant ->
                restaurant.name.contains(query, ignoreCase = true) ||
                restaurant.description.contains(query, ignoreCase = true) ||
                restaurant.category.contains(query, ignoreCase = true) ||
                restaurant.tags.any { it.contains(query, ignoreCase = true) }
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRestaurantsByCategory(category: String): Result<List<Restaurant>> {
        return try {
            val filtered = mockRestaurants.filter { 
                it.category.equals(category, ignoreCase = true) 
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNearbyRestaurants(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Result<List<Restaurant>> {
        return try {
            val nearby = mockRestaurants.filter { restaurant ->
                val distance = calculateDistance(
                    latitude, longitude,
                    restaurant.latitude, restaurant.longitude
                )
                distance <= radiusKm
            }
            Result.success(nearby.sortedBy { restaurant ->
                calculateDistance(
                    latitude, longitude,
                    restaurant.latitude, restaurant.longitude
                )
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeRestaurants(): Flow<List<Restaurant>> = flow {
        emit(mockRestaurants)
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
