package com.yei.dev.weather_app.domain.usecase

import com.yei.dev.weather_app.domain.model.ForecastDay
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateAverageTemperatureUseCaseTest {

    private val sut = CalculateAverageTemperatureUseCase()

    @Test
    fun `invoke should calculate average of max temperatures correctly`() {
        // Given
        val averageTemperatureExpected = 22.0
        val forecastDays = providesForecastDays(
            maxTemps = listOf(22.0, 23.0, 21.0)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should round to 1 decimal place`() {
        // Given
        val averageTemperatureExpected = 22.5
        val forecastDays = providesForecastDays(
            maxTemps = listOf(22.3, 23.7, 21.5)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should round down when necessary`() {
        // Given
        val averageTemperatureExpected = 22.2
        val forecastDays = providesForecastDays(
            maxTemps = listOf(22.2, 23.2, 21.2)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should round up when necessary`() {
        // Given
        val averageTemperatureExpected = 22.3
        val forecastDays = providesForecastDays(
            maxTemps = listOf(22.3, 23.3, 21.3)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should return 0 when list is empty`() {
        // Given
        val averageTemperatureExpected = 0.0
        val forecastDays = emptyList<ForecastDay>()

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should handle single day forecast`() {
        // Given
        val averageTemperatureExpected = 25.5
        val forecastDays = providesForecastDays(
            maxTemps = listOf(25.5)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should handle negative temperatures`() {
        // Given
        val averageTemperatureExpected = -5.0
        val forecastDays = providesForecastDays(
            maxTemps = listOf(-5.0, -3.0, -7.0)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should handle mix of positive and negative temperatures`() {
        // Given
        val averageTemperatureExpected = 2.0
        val forecastDays = providesForecastDays(
            maxTemps = listOf(-2.0, 5.0, 3.0)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should handle very high temperatures`() {
        // Given
        val averageTemperatureExpected = 46.0
        val forecastDays = providesForecastDays(
            maxTemps = listOf(45.0, 47.0, 46.0)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should handle very low temperatures`() {
        // Given
        val averageTemperatureExpected = -27.7
        val forecastDays = providesForecastDays(
            maxTemps = listOf(-25.0, -30.0, -28.0)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, DELTA)
    }

    @Test
    fun `invoke should handle decimal precision edge case`() {
        // Given
        val averageTemperatureExpected = 22.1
        val forecastDays = providesForecastDays(
            maxTemps = listOf(22.15, 23.15, 21.15)
        )

        // When
        val result = sut(forecastDays)

        // Then
        assertEquals(averageTemperatureExpected, result, 0.01)
    }

    @Test
    fun `invoke should only use maxTemp field from ForecastDay`() {
        // Given
        val forecastDays = listOf(
            ForecastDay(
                date = "2024-12-08",
                dateEpoch = 1733616000L,
                maxTemp = 25.0,
                minTemp = 10.0,  // Should be ignored
                avgTemp = 17.5,  // Should be ignored
                condition = "Sunny",
                iconUrl = "https://example.com/icon.png"
            ),
            ForecastDay(
                date = "2024-12-09",
                dateEpoch = 1733702400L,
                maxTemp = 27.0,
                minTemp = 12.0,  // Should be ignored
                avgTemp = 19.5,  // Should be ignored
                condition = "Cloudy",
                iconUrl = "https://example.com/icon.png"
            ),
            ForecastDay(
                date = "2024-12-10",
                dateEpoch = 1733788800L,
                maxTemp = 26.0,
                minTemp = 11.0,  // Should be ignored
                avgTemp = 18.5,  // Should be ignored
                condition = "Rainy",
                iconUrl = "https://example.com/icon.png"
            )
        )

        // When
        val result = sut(forecastDays)

        // Then
        // (25.0 + 27.0 + 26.0) / 3 = 78.0 / 3 = 26.0
        assertEquals(26.0, result, 0.01)
    }

    private fun providesForecastDays(
        maxTemps: List<Double>
    ): List<ForecastDay> {
        return maxTemps.mapIndexed { index, maxTemp ->
            ForecastDay(
                date = "2024-12-0${8 + index}",
                dateEpoch = 1733616000L + (index * 86400L),
                maxTemp = maxTemp,
                minTemp = maxTemp - 7.0,
                avgTemp = maxTemp - 3.5,
                condition = "Partly cloudy",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
            )
        }
    }

    companion object {
        private const val DELTA = 0.01
    }
}
