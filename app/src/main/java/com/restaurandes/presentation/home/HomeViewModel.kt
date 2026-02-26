package com.restaurandes.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurandes.data.analytics.AnalyticsService
import com.restaurandes.domain.model.Restaurant
import com.restaurandes.domain.usecase.GetNearbyRestaurantsUseCase
import com.restaurandes.domain.usecase.GetRestaurantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase,
    private val getNearbyRestaurantsUseCase: GetNearbyRestaurantsUseCase,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRestaurants()
        // Track BQ2: Home section view
        analyticsService.logSectionView(AnalyticsService.AppSection.HOME, null)
    }

    fun loadRestaurants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getRestaurantsUseCase().fold(
                onSuccess = { restaurants ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            restaurants = restaurants,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }

    fun loadNearbyRestaurants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getNearbyRestaurantsUseCase(radiusKm = 5.0).fold(
                onSuccess = { restaurants ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            restaurants = restaurants,
                            error = null,
                            selectedFilter = FilterType.Nearby
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Location unavailable"
                        )
                    }
                }
            )
        }
    }

    fun filterRestaurants(filterType: FilterType) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedFilter = filterType) }
            
            // Track BQ2: Filter interaction
            analyticsService.logFilterUsed(filterType.name, null)
            analyticsService.logSectionInteraction(
                AnalyticsService.AppSection.HOME,
                "filter_${filterType.name.lowercase()}",
                null
            )
            
            when (filterType) {
                FilterType.All -> loadRestaurants()
                FilterType.Nearby -> loadNearbyRestaurants()
                FilterType.Open -> filterByOpen()
                FilterType.TopRated -> filterByRating()
                FilterType.Economic -> filterByPrice()
            }
        }
    }
    
    fun onRestaurantClick(restaurantId: String, restaurantName: String) {
        // Track BQ3: Restaurant view
        analyticsService.logRestaurantView(restaurantId, restaurantName, null)
    }

    private fun filterByOpen() {
        val currentRestaurants = _uiState.value.restaurants
        val filtered = currentRestaurants.filter { it.isOpen }
        _uiState.update { it.copy(restaurants = filtered) }
    }

    private fun filterByRating() {
        val currentRestaurants = _uiState.value.restaurants
        val sorted = currentRestaurants.sortedByDescending { it.rating }
        _uiState.update { it.copy(restaurants = sorted) }
    }

    private fun filterByPrice() {
        val currentRestaurants = _uiState.value.restaurants
        val sorted = currentRestaurants.sortedBy { it.priceRange }
        _uiState.update { it.copy(restaurants = sorted) }
    }
}
