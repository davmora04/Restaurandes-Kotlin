package com.restaurandes.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object RestaurantDetail : Screen("restaurant/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant/$restaurantId"
    }
    data object Map : Screen("map")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Profile : Screen("profile")
}
