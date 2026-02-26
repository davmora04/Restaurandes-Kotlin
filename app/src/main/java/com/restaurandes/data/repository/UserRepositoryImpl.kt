package com.restaurandes.data.repository

import com.restaurandes.domain.model.User
import com.restaurandes.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    
    // Mock users database
    private val users = mutableMapOf<String, User>()

    override suspend fun getCurrentUser(): Result<User?> {
        return Result.success(_currentUser.value)
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // TODO: Replace with Firebase Authentication
            // Mock authentication
            val user = users[email] ?: User(
                id = email.hashCode().toString(),
                email = email,
                name = email.substringBefore("@")
            )
            users[email] = user
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            // TODO: Replace with Firebase Authentication
            val user = User(
                id = email.hashCode().toString(),
                email = email,
                name = name
            )
            users[email] = user
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            _currentUser.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            users[user.email] = user
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addFavoriteRestaurant(restaurantId: String): Result<Unit> {
        return try {
            val currentUser = _currentUser.value ?: return Result.failure(Exception("User not logged in"))
            val updatedFavorites = currentUser.favoriteRestaurants.toMutableList().apply {
                if (!contains(restaurantId)) add(restaurantId)
            }
            val updatedUser = currentUser.copy(favoriteRestaurants = updatedFavorites)
            _currentUser.value = updatedUser
            users[currentUser.email] = updatedUser
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFavoriteRestaurant(restaurantId: String): Result<Unit> {
        return try {
            val currentUser = _currentUser.value ?: return Result.failure(Exception("User not logged in"))
            val updatedFavorites = currentUser.favoriteRestaurants.toMutableList().apply {
                remove(restaurantId)
            }
            val updatedUser = currentUser.copy(favoriteRestaurants = updatedFavorites)
            _currentUser.value = updatedUser
            users[currentUser.email] = updatedUser
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeCurrentUser(): Flow<User?> {
        return _currentUser.asStateFlow()
    }
}
