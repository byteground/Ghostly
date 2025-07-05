package com.ghostly.settings.models

import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val id: String,
    val name: String,
    val description: String,
)

@Serializable
data class RolesResponse(
    val roles: List<Role>,
)

enum class StaffRole {
    CONTRIBUTOR,
    AUTHOR,
    EDITOR,
    ADMINISTRATOR
}