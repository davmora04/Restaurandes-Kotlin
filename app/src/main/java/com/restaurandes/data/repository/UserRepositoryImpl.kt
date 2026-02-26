package com.restaurandes.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.restaurandes.data.analytics.AnalyticsService
import com.restaurandes.domain.model.User
import com.restaurandes.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val analyticsService: AnalyticsService
) : UserRepository {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    
    private val _currentUser = MutableStateFlow<User?>(null)
    
    init {
        // Listen to auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Load user data from Firestore or create from Firebase user
                _currentUser.value = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User"
                )
            } else {
                _currentUser.value = null
            }
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Try to load from Firestore
                val doc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                
                val user = if (doc.exists()) {
                    User(
                        id = firebaseUser.uid,
                        email = doc.getString("email") ?: firebaseUser.email ?: "",
                        name = doc.getString("name") ?: firebaseUser.displayName ?: "",
                        favoriteRestaurants = (doc.get("favoriteRestaurants") as? List<*>)
                            ?.filterIsInstance<String>() ?: emptyList(),
                        dietaryPreferences = (doc.get("dietaryPreferences") as? List<*>)
                            ?.filterIsInstance<String>() ?: emptyList()
                    )
                } else {
                    // Create user document if doesn't exist
                    val newUser = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User"
                    )
                    saveUserToFirestore(newUser)
                    newUser
                }
                _currentUser.value = user
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Authentication failed"))
            
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                name = firebaseUser.displayName ?: email.substringBefore("@")
            )
            
            // Load full user data from Firestore
            getCurrentUser()
            
            // Track login
            analyticsService.logSignIn("email", firebaseUser.uid)
            analyticsService.logUserSession(firebaseUser.uid)
            
            Result.success(_currentUser.value ?: user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Registration failed"))
            
            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name
            )
            
            // Save to Firestore
            saveUserToFirestore(user)
            _currentUser.value = user
            
            // Track signup
            analyticsService.logSignUp("email", firebaseUser.uid)
            analyticsService.logUserSession(firebaseUser.uid)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            auth.signOut()
            _currentUser.value = null
            
            // Track session end
            userId?.let {
                analyticsService.logUserSessionEnd(it, 0) // TODO: Track actual duration
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            saveUserToFirestore(user)
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
            
            // Update in Firestore
            firestore.collection("users")
                .document(currentUser.id)
                .update("favoriteRestaurants", updatedFavorites)
                .await()
            
            _currentUser.value = updatedUser
            
            // Track BQ3: Favorite added
            analyticsService.logRestaurantFavorited(restaurantId, "", currentUser.id)
            
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
            
            // Update in Firestore
            firestore.collection("users")
                .document(currentUser.id)
                .update("favoriteRestaurants", updatedFavorites)
                .await()
            
            _currentUser.value = updatedUser
            
            // Track unfavorite
            analyticsService.logRestaurantUnfavorited(restaurantId, currentUser.id)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeCurrentUser(): Flow<User?> {
        return _currentUser.asStateFlow()
    }
    
    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users")
            .document(user.id)
            .set(
                mapOf(
                    "email" to user.email,
                    "name" to user.name,
                    "favoriteRestaurants" to user.favoriteRestaurants,
                    "dietaryPreferences" to user.dietaryPreferences,
                    "createdAt" to System.currentTimeMillis()
                )
            )
            .await()
    }
}
