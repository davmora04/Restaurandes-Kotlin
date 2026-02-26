package com.restaurandes.domain.usecase

import com.restaurandes.domain.model.Restaurant
import com.restaurandes.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRestaurantsUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(): Result<List<Restaurant>> {
        return repository.getRestaurants()
    }
}
