package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.ConditionDto
import com.yei.dev.weather_app.data.remote.dto.DayDto
import com.yei.dev.weather_app.data.remote.dto.ForecastDayDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ForecastDayMapperTest {

    private val sut = ForecastDayMapper()

    @Test
    fun `convert should map ForecastDayDto to ForecastDay correctly`() {
        // Given
        val dto = providesForecastDayDto()

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals("2024-12-08", result.date)
        assertEquals(1733616000L, result.dateEpoch)
        assertEquals(22.0, result.maxTemp, 0.01)
        assertEquals(15.0, result.minTemp, 0.01)
        assertEquals(18.5, result.avgTemp, 0.01)
        assertEquals("Partly cloudy", result.condition)
        assertEquals("https://cdn.weatherapi.com/weather/64x64/day/116.png", result.iconUrl)
    }

    @Test
    fun `convert should prepend https to icon URL`() {
        // Given
        val dto = providesForecastDayDto(
            icon = "//cdn.weatherapi.com/weather/64x64/day/113.png"
        )

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals("https://cdn.weatherapi.com/weather/64x64/day/113.png", result.iconUrl)
    }

    @Test
    fun `convert should handle different temperature ranges`() {
        // Given
        val dto = providesForecastDayDto(
            maxTempC = 30.0,
            minTempC = 20.0,
            avgTempC = 25.0
        )

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(30.0, result.maxTemp, 0.01)
        assertEquals(20.0, result.minTemp, 0.01)
        assertEquals(25.0, result.avgTemp, 0.01)
    }

    @Test
    fun `convert should handle negative temperatures`() {
        // Given
        val dto = providesForecastDayDto(
            maxTempC = -2.0,
            minTempC = -10.0,
            avgTempC = -6.0
        )

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(-2.0, result.maxTemp, 0.01)
        assertEquals(-10.0, result.minTemp, 0.01)
        assertEquals(-6.0, result.avgTemp, 0.01)
    }

    @Test
    fun `convert should map different dates correctly`() {
        // Given
        val dates = listOf(
            "2024-12-08" to 1733616000L,
            "2024-12-09" to 1733702400L,
            "2024-12-10" to 1733788800L
        )

        dates.forEach { (date, epoch) ->
            // Given
            val dto = providesForecastDayDto(date = date, dateEpoch = epoch)

            // When
            val result = sut.convert(dto)

            // Then
            assertEquals(date, result.date)
            assertEquals(epoch, result.dateEpoch)
        }
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
            val dto = providesForecastDayDto(conditionText = condition)

            // When
            val result = sut.convert(dto)

            // Then
            assertEquals(condition, result.condition)
        }
    }

    @Test
    fun `convertList should map multiple ForecastDayDto correctly`() {
        // Given
        val dtoList = listOf(
            providesForecastDayDto(date = "2024-12-08", maxTempC = 22.0),
            providesForecastDayDto(date = "2024-12-09", maxTempC = 23.0),
            providesForecastDayDto(date = "2024-12-10", maxTempC = 21.0)
        )

        // When
        val result = sut.convertList(dtoList)

        // Then
        assertEquals(3, result.size)
        assertEquals("2024-12-08", result[0].date)
        assertEquals("2024-12-09", result[1].date)
        assertEquals("2024-12-10", result[2].date)
        assertEquals(22.0, result[0].maxTemp, 0.01)
        assertEquals(23.0, result[1].maxTemp, 0.01)
        assertEquals(21.0, result[2].maxTemp, 0.01)
    }

    private fun providesForecastDayDto(
        date: String = "2024-12-08",
        dateEpoch: Long = 1733616000L,
        maxTempC: Double = 22.0,
        minTempC: Double = 15.0,
        avgTempC: Double = 18.5,
        conditionText: String = "Partly cloudy",
        icon: String = "//cdn.weatherapi.com/weather/64x64/day/116.png",
        code: Int = 1003
    ) = ForecastDayDto(
        date = date,
        dateEpoch = dateEpoch,
        day = DayDto(
            maxTempC = maxTempC,
            minTempC = minTempC,
            avgTempC = avgTempC,
            condition = ConditionDto(
                text = conditionText,
                icon = icon,
                code = code
            )
        )
    )
}
