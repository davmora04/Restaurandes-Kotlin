package com.restaurandes.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurandes.domain.model.Restaurant
import com.restaurandes.domain.repository.UserRepository
import com.restaurandes.domain.usecase.GetRestaurantsUseCase
import com.restaurandes.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Restaurant> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val isLoggedIn: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeUserFavorites()
    }

    private fun observeUserFavorites() {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collectLatest { user ->
                if (user == null) {
                    _uiState.value = FavoritesUiState(
                        isLoading = false,
                        favorites = emptyList(),
                        favoriteIds = emptySet(),
                        isLoggedIn = false,
                        error = "Debes iniciar sesión para ver tus favoritos."
                    )
                    return@collectLatest
                }

                loadFavorites(user.favoriteRestaurants)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().getOrNull()
            if (user == null) {
                _uiState.value = FavoritesUiState(
                    isLoading = false,
                    favorites = emptyList(),
                    favoriteIds = emptySet(),
                    isLoggedIn = false,
                    error = "Debes iniciar sesión para ver tus favoritos."
                )
            } else {
                loadFavorites(user.favoriteRestaurants)
            }
        }
    }

    private suspend fun loadFavorites(favoriteRestaurantIds: List<String>) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            favoriteIds = favoriteRestaurantIds.toSet(),
            isLoggedIn = true
        )

        getRestaurantsUseCase().fold(
            onSuccess = { restaurants ->
                val favorites = restaurants.filter { it.id in favoriteRestaurantIds }
                _uiState.value = FavoritesUiState(
                    isLoading = false,
                    favorites = favorites,
                    favoriteIds = favoriteRestaurantIds.toSet(),
                    isLoggedIn = true,
                    error = null
                )
            },
            onFailure = { exception ->
                _uiState.value = FavoritesUiState(
                    isLoading = false,
                    favorites = emptyList(),
                    favoriteIds = favoriteRestaurantIds.toSet(),
                    isLoggedIn = true,
                    error = exception.message ?: "No se pudieron cargar los favoritos."
                )
            }
        )
    }

    fun toggleFavorite(restaurantId: String) {
        val isFavorite = _uiState.value.favoriteIds.contains(restaurantId)

        viewModelScope.launch {
            toggleFavoriteUseCase(restaurantId, isFavorite).fold(
                onSuccess = {
                    val newFavoriteIds = _uiState.value.favoriteIds.toMutableSet().apply {
                        if (isFavorite) remove(restaurantId) else add(restaurantId)
                    }

                    val newFavorites = _uiState.value.favorites.filter { it.id != restaurantId }

                    _uiState.value = _uiState.value.copy(
                        favoriteIds = newFavoriteIds,
                        favorites = newFavorites
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "No se pudo actualizar el favorito."
                    )
                }
            )
        }
    }
}