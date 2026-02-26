package com.restaurandes.domain.usecase

import com.restaurandes.domain.repository.UserRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(restaurantId: String, isFavorite: Boolean): Result<Unit> {
        return if (isFavorite) {
            repository.removeFavoriteRestaurant(restaurantId)
        } else {
            repository.addFavoriteRestaurant(restaurantId)
        }
    }
}
