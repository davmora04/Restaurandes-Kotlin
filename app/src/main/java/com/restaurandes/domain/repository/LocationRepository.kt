package com.restaurandes.domain.repository

import com.restaurandes.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<Location>
    fun observeLocation(): Flow<Location>
    fun hasLocationPermission(): Boolean
}
