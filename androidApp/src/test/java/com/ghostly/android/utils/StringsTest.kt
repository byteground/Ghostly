package com.ghostly.android.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class StringsTest {
    @Test
    fun `isValidGhostDomain accepts hostname without scheme`() {
        assertTrue(isValidGhostDomain("blog.example.com"))
    }

    @Test
    fun `isValidGhostDomain accepts https with no ghost path`() {
        assertTrue(isValidGhostDomain("https://blog.example.com"))
    }

    @Test
    fun `isValidGhostDomain accepts https with ghost path`() {
        assertTrue(isValidGhostDomain("https://blog.example.com/ghost"))
    }

    @Test
    fun `normalizeGhostDomainInput ensures https and ghost path`() {
        assertEquals(
            "https://blog.example.com/ghost",
            normalizeGhostDomainInput("blog.example.com")
        )
        assertEquals(
            "https://blog.example.com/ghost",
            normalizeGhostDomainInput("https://blog.example.com/")
        )
        assertEquals(
            "https://blog.example.com/ghost",
            normalizeGhostDomainInput("https://blog.example.com/ghost")
        )
    }

    @Test
    fun `isValidGhostDomain rejects invalid inputs`() {
        assertFalse(isValidGhostDomain("http://"))
        assertFalse(isValidGhostDomain("example"))
        assertFalse(isValidGhostDomain("ftp://blog.example.com"))
        assertFalse(isValidGhostDomain(""))
        assertFalse(isValidGhostDomain("   "))
    }
}

