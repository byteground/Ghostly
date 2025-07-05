package com.ghostly.settings.models

import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val id: String,
    val name: String,
    val description: String
)

@Serializable
data class RolesResponse(
    val roles: List<Role>
)

data class UsersAndRoles(
    val role: Role,
    val users: List<User>,
)