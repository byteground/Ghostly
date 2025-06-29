package com.ghostly.settings.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Invite(
    @SerialName("role_id")
    val roleId: String,
    val email: String
)

@Serializable
data class InviteRequestWrapper(
    val invites: List<Invite>
)