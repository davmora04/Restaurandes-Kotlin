package com.restaurandes.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurandes.data.analytics.AnalyticsService
import com.restaurandes.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        // Limpiar espacios en blanco
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPassword = password.trim()
        val cleanConfirmPassword = confirmPassword.trim()
        
        // Validaciones
        if (cleanName.isBlank() || cleanEmail.isBlank() || cleanPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Todos los campos son requeridos")
            return
        }

        // Validar formato de email
        if (!isValidEmail(cleanEmail)) {
            _uiState.value = _uiState.value.copy(error = "El email debe tener formato válido (ejemplo: usuario@correo.com)")
            return
        }

        if (cleanPassword != cleanConfirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Las contraseñas no coinciden")
            return
        }

        if (cleanPassword.length < 6) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = userRepository.signUp(cleanEmail, cleanPassword, cleanName)
                result.fold(
                    onSuccess = { user ->
                        // Analytics handled by repository
                        _uiState.value = RegisterUiState(isSuccess = true)
                    },
                    onFailure = { error ->
                        val errorMessage = when {
                            error.message?.contains("badly formatted", ignoreCase = true) == true -> 
                                "Email inválido. Verifica el formato (ejemplo: usuario@correo.com)"
                            error.message?.contains("email address is already", ignoreCase = true) == true -> 
                                "Este email ya está registrado. Intenta iniciar sesión."
                            error.message?.contains("network", ignoreCase = true) == true -> 
                                "Error de conexión. Verifica tu internet."
                            else -> error.message ?: "Error al registrar usuario"
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error inesperado"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailPattern.matches(email)
    }
}
