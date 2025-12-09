package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.ConditionDto
import com.yei.dev.weather_app.data.remote.dto.CurrentWeatherDto
import com.yei.dev.weather_app.data.remote.dto.DayDto
import com.yei.dev.weather_app.data.remote.dto.ForecastDayDto
import com.yei.dev.weather_app.data.remote.dto.ForecastDto
import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.dto.WeatherForecastResponseDto
import com.yei.dev.weather_app.domain.model.CurrentWeather
import com.yei.dev.weather_app.domain.model.ForecastDay
import com.yei.dev.weather_app.domain.model.Location
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherForecastMapperTest {

    private val locationMapper = mockk<Converter<LocationDto, Location>>()
    private val currentWeatherMapper = mockk<Converter<CurrentWeatherDto, CurrentWeather>>()
    private val forecastDayMapper = mockk<Converter<ForecastDayDto, ForecastDay>>()

    private val sut = WeatherForecastMapper(
        locationMapper = locationMapper,
        currentWeatherMapper = currentWeatherMapper,
        forecastDayMapper = forecastDayMapper
    )

    @Test
    fun `convert should map WeatherForecastResponseDto to WeatherForecast correctly`() {
        // Given
        val dto = providesWeatherForecastResponseDto()
        val expectedLocation = providesLocation()
        val expectedCurrentWeather = providesCurrentWeather()
        val expectedForecastDays = providesForecastDayList()

        every { locationMapper.convert(dto.location) } returns expectedLocation
        every { currentWeatherMapper.convert(dto.current) } returns expectedCurrentWeather
        every { forecastDayMapper.convertList(dto.forecast.forecastDay) } returns expectedForecastDays

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(expectedLocation, result.location)
        assertEquals(expectedCurrentWeather, result.currentWeather)
        assertEquals(expectedForecastDays, result.forecastDays)
    }

    @Test
    fun `convert should call locationMapper with correct DTO`() {
        // Given
        val dto = providesWeatherForecastResponseDto()
        every { locationMapper.convert(any()) } returns providesLocation()
        every { currentWeatherMapper.convert(any()) } returns providesCurrentWeather()
        every { forecastDayMapper.convertList(any()) } returns providesForecastDayList()

        // When
        sut.convert(dto)

        // Then
        verify(exactly = 1) { locationMapper.convert(dto.location) }
    }

    @Test
    fun `convert should call currentWeatherMapper with correct DTO`() {
        // Given
        val dto = providesWeatherForecastResponseDto()
        every { locationMapper.convert(any()) } returns providesLocation()
        every { currentWeatherMapper.convert(any()) } returns providesCurrentWeather()
        every { forecastDayMapper.convertList(any()) } returns providesForecastDayList()

        // When
        sut.convert(dto)

        // Then
        verify(exactly = 1) { currentWeatherMapper.convert(dto.current) }
    }

    @Test
    fun `convert should call forecastDayMapper with correct DTO list`() {
        // Given
        val dto = providesWeatherForecastResponseDto()
        every { locationMapper.convert(any()) } returns providesLocation()
        every { currentWeatherMapper.convert(any()) } returns providesCurrentWeather()
        every { forecastDayMapper.convertList(any()) } returns providesForecastDayList()

        // When
        sut.convert(dto)

        // Then
        verify(exactly = 1) { forecastDayMapper.convertList(dto.forecast.forecastDay) }
    }

    @Test
    fun `convert should handle forecast with 3 days`() {
        // Given
        val dto = providesWeatherForecastResponseDto(forecastDaysCount = 3)
        val expectedForecastDays = providesForecastDayList(count = 3)

        every { locationMapper.convert(any()) } returns providesLocation()
        every { currentWeatherMapper.convert(any()) } returns providesCurrentWeather()
        every { forecastDayMapper.convertList(any()) } returns expectedForecastDays

        // When
        val result = sut.convert(dto)

        // Then
        assertEquals(3, result.forecastDays.size)
    }

    @Test
    fun `convert should handle different locations`() {
        // Given
        val dto = providesWeatherForecastResponseDto()
        val locations = listOf(
            providesLocation(name = "Mosquera"),
            providesLocation(name = "Bogotá"),
            providesLocation(name = "Medellín")
        )

        locations.forEach { location ->
            every { locationMapper.convert(any()) } returns location
            every { currentWeatherMapper.convert(any()) } returns providesCurrentWeather()
            every { forecastDayMapper.convertList(any()) } returns providesForecastDayList()

            // When
            val result = sut.convert(dto)

            // Then
            assertEquals(location.name, result.location.name)
        }
    }

    private fun providesWeatherForecastResponseDto(
        forecastDaysCount: Int = 3
    ) = WeatherForecastResponseDto(
        location = LocationDto(
            id = null,
            name = "Mosquera",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7059,
            lon = -74.2302,
            url = null
        ),
        current = CurrentWeatherDto(
            tempC = 18.5,
            feelsLikeC = 17.8,
            condition = ConditionDto(
                text = "Partly cloudy",
                icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                code = 1003
            )
        ),
        forecast = ForecastDto(
            forecastDay = List(forecastDaysCount) { index ->
                ForecastDayDto(
                    date = "2024-12-0${8 + index}",
                    dateEpoch = 1733616000L + (index * 86400L),
                    day = DayDto(
                        maxTempC = 22.0 + index,
                        minTempC = 15.0 + index,
                        avgTempC = 18.5 + index,
                        condition = ConditionDto(
                            text = "Partly cloudy",
                            icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                            code = 1003
                        )
                    )
                )
            }
        )
    )

    private fun providesLocation(
        name: String = "Mosquera"
    ) = Location(
        id = null,
        name = name,
        region = "Cundinamarca",
        country = "Colombia",
        lat = 4.7059,
        lon = -74.2302
    )

    private fun providesCurrentWeather() = CurrentWeather(
        temperature = 18.5,
        feelsLike = 17.8,
        condition = "Partly cloudy",
        iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
    )

    private fun providesForecastDayList(count: Int = 3) = List(count) { index ->
        ForecastDay(
            date = "2024-12-0${8 + index}",
            dateEpoch = 1733616000L + (index * 86400L),
            maxTemp = 22.0 + index,
            minTemp = 15.0 + index,
            avgTemp = 18.5 + index,
            condition = "Partly cloudy",
            iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
        )
    }
}
