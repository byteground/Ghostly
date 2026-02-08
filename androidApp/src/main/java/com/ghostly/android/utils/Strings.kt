package com.ghostly.android.utils

import android.util.Patterns
import java.util.Locale
import java.util.regex.Pattern

// Accepts hosts with or without scheme and optional trailing /ghost
// Examples accepted:
// - blog.example.com
// - https://blog.example.com
// - https://blog.example.com/ghost
// - http://blog.example.com/ghost
private val RELAXED_GHOST_DOMAIN = Pattern.compile(
    "^(?:(?:https?)://)?" +
            "((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+" +
            "[A-Za-z]{2,}(?:/ghost)?/?$"
)

fun isValidEmail(target: CharSequence?): Boolean =
    target?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } == true

fun isValidGhostDomain(target: CharSequence?): Boolean =
    target?.let { RELAXED_GHOST_DOMAIN.matcher(it.trim()).matches() } == true

fun normalizeGhostDomainInput(input: String): String {
    // Trim and ensure scheme and trailing /ghost
    var url = input.trim()
    if (url.isEmpty()) return url

    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "https://$url"
    }

    // Remove any trailing slashes to standardize, then append /ghost
    url = url.removeSuffix("/")
    if (!url.endsWith("/ghost")) {
        url = "$url/ghost"
    }

    return url
}

fun String.capitalize(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
