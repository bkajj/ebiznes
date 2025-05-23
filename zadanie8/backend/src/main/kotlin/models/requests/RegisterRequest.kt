package models.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)