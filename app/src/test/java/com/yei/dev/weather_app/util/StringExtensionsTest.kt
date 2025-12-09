package com.yei.dev.weather_app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `toShortDateFormat should format valid ISO date correctly`() {
        // Given
        val isoDate = "2024-12-08"

        // When
        val result = isoDate.toShortDateFormat()

        // Then
        assertEquals("08/12", result)
    }

    @Test
    fun `toShortDateFormat should handle single digit day and month`() {
        // Given
        val isoDate = "2024-01-05"

        // When
        val result = isoDate.toShortDateFormat()

        // Then
        assertEquals("05/01", result)
    }

    @Test
    fun `toShortDateFormat should return original string for invalid format`() {
        // Given
        val invalidDate = "invalid-date"

        // When
        val result = invalidDate.toShortDateFormat()

        // Then
        assertEquals("invalid-date", result)
    }

    @Test
    fun `toShortDateFormat should return original string for incomplete date`() {
        // Given
        val incompleteDate = "2024-12"

        // When
        val result = incompleteDate.toShortDateFormat()

        // Then
        assertEquals("2024-12", result)
    }

    @Test
    fun `toShortDateFormat should return original string for empty string`() {
        // Given
        val emptyDate = ""

        // When
        val result = emptyDate.toShortDateFormat()

        // Then
        assertEquals("", result)
    }
}
