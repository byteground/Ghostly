package com.ghostly.login.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SiteDetails(
    val title: String,
    val icon: String,
)

@Serializable
data class SiteResponse(
    @SerialName("site")
    val siteDetails: SiteDetails
)