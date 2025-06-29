package com.ghostly.settings.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val roles: List<Role>
)

@Serializable
data class UsersResponse(
    val users: List<User>
)