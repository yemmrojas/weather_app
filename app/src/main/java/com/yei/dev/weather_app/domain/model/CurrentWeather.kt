package com.yei.dev.weather_app.domain.model

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val condition: String,
    val iconUrl: String
)
