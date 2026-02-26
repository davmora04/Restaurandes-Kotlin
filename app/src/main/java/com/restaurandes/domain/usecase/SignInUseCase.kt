package com.restaurandes.domain.usecase

import com.restaurandes.domain.model.User
import com.restaurandes.domain.repository.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.signIn(email, password)
    }
}
