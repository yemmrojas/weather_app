package com.yei.dev.weather_app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecastResponseDto(
    @SerialName("location")
    val location: LocationDto,
    @SerialName("current")
    val current: CurrentWeatherDto,
    @SerialName("forecast")
    val forecast: ForecastDto
)

@Serializable
data class ForecastDto(
    @SerialName("forecastday")
    val forecastDay: List<ForecastDayDto>
)
