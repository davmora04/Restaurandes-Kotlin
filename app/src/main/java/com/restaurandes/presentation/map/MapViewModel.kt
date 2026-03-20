package com.restaurandes.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurandes.domain.model.Restaurant
import com.restaurandes.domain.usecase.GetRestaurantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val isLoading: Boolean = false,
    val restaurants: List<Restaurant> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetRestaurantsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadRestaurants()
    }

    fun loadRestaurants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getRestaurantsUseCase().fold(
                onSuccess = { restaurants ->
                    _uiState.value = MapUiState(
                        isLoading = false,
                        restaurants = restaurants,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _uiState.value = MapUiState(
                        isLoading = false,
                        restaurants = emptyList(),
                        error = exception.message ?: "No se pudieron cargar los restaurantes"
                    )
                }
            )
        }
    }
}