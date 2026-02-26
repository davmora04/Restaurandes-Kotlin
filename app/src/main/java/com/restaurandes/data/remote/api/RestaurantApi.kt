package com.restaurandes.data.remote.api

import com.restaurandes.data.remote.dto.RestaurantDto
import retrofit2.Response
import retrofit2.http.*

interface RestaurantApi {
    @GET("restaurants")
    suspend fun getRestaurants(): Response<List<RestaurantDto>>
    
    @GET("restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: String): Response<RestaurantDto>
    
    @GET("restaurants/search")
    suspend fun searchRestaurants(@Query("q") query: String): Response<List<RestaurantDto>>
    
    @GET("restaurants/category/{category}")
    suspend fun getRestaurantsByCategory(@Path("category") category: String): Response<List<RestaurantDto>>
    
    @GET("restaurants/nearby")
    suspend fun getNearbyRestaurants(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radiusKm: Double
    ): Response<List<RestaurantDto>>
}
