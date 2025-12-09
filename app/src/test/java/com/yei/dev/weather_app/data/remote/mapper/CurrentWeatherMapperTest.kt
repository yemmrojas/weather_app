package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.ConditionDto
import com.yei.dev.weather_app.data.remote.dto.CurrentWeatherDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrentWeatherMapperTest {

    private val sut = CurrentWeatherMapper()

    @Test
    fun `convert should map CurrentWeatherDto to CurrentWeather correctly`() {
        // Given
        val dto = providesCurrentWeatherDto()
        val iconUrlExpected = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
        val conditionExpected = "Partly cloudy"
        val temperatureExpected = 18.5
        val feelsLikeExpected = 17.8

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(temperatureExpected, result.temperature, 0.01)
        assertEquals(feelsLikeExpected, result.feelsLike, 0.01)
        assertEquals(conditionExpected, result.condition)
        assertEquals(iconUrlExpected, result.iconUrl)
    }

    @Test
    fun `convert should prepend https to icon URL`() {
        // Given
        val iconUrl = "//cdn.weatherapi.com/weather/64x64/day/116.png"
        val iconUrlExpected = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
        val dto = providesCurrentWeatherDto(
            icon = iconUrl
        )

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(iconUrlExpected, result.iconUrl)
    }

    @Test
    fun `convert should handle different temperature values`() {
        // Given
        val dto = providesCurrentWeatherDto(
            tempC = 25.3,
            feelsLikeC = 26.1
        )

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(25.3, result.temperature, 0.01)
        assertEquals(26.1, result.feelsLike, 0.01)
    }

    @Test
    fun `convert should handle negative temperatures`() {
        // Given
        val dto = providesCurrentWeatherDto(
            tempC = -5.0,
            feelsLikeC = -8.5
        )

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(-5.0, result.temperature, 0.01)
        assertEquals(-8.5, result.feelsLike, 0.01)
    }

    @Test
    fun `convert should map different weather conditions`() {
        // Given
        val conditions = listOf(
            "Sunny",
            "Cloudy",
            "Rainy",
            "Snowy",
            "Partly cloudy"
        )

        conditions.forEach { condition ->
            // Given
            val dto = providesCurrentWeatherDto(conditionText = condition)

            // When
            val result = sut.convert(dto)

            // Then
            assertEquals(condition, result.condition)
        }
    }

    private fun providesCurrentWeatherDto(
        tempC: Double = 18.5,
        feelsLikeC: Double = 17.8,
        conditionText: String = "Partly cloudy",
        icon: String = "//cdn.weatherapi.com/weather/64x64/day/116.png",
        code: Int = 1003
    ) = CurrentWeatherDto(
        tempC = tempC,
        feelsLikeC = feelsLikeC,
        condition = ConditionDto(
            text = conditionText,
            icon = icon,
            code = code
        )
    )
}
