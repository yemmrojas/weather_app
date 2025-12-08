package com.yei.dev.weather_app.domain.model

data class WeatherForecast(
    val location: Location,
    val currentWeather: CurrentWeather,
    val forecastDays: List<ForecastDay>
)
