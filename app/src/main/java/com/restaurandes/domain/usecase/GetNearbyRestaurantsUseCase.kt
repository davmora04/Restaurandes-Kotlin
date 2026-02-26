package com.restaurandes.domain.usecase

import com.restaurandes.domain.model.Restaurant
import com.restaurandes.domain.repository.LocationRepository
import com.restaurandes.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetNearbyRestaurantsUseCase @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(radiusKm: Double = 5.0): Result<List<Restaurant>> {
        return locationRepository.getCurrentLocation().fold(
            onSuccess = { location ->
                restaurantRepository.getNearbyRestaurants(
                    location.latitude,
                    location.longitude,
                    radiusKm
                )
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}
