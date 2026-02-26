package com.restaurandes.domain.usecase

import com.restaurandes.domain.model.Restaurant
import com.restaurandes.domain.repository.RestaurantRepository
import javax.inject.Inject

class SearchRestaurantsUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(query: String): Result<List<Restaurant>> {
        return repository.searchRestaurants(query)
    }
}
