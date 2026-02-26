package com.restaurandes.presentation.home

import com.restaurandes.domain.model.Restaurant

data class HomeUiState(
    val isLoading: Boolean = false,
    val restaurants: List<Restaurant> = emptyList(),
    val error: String? = null,
    val userLocation: Pair<Double, Double>? = null,
    val selectedFilter: FilterType = FilterType.All
)

enum class FilterType {
    All,
    Nearby,
    Open,
    TopRated,
    Economic
}
