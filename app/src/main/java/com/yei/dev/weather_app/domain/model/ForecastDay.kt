package com.yei.dev.weather_app.domain.model

data class ForecastDay(
    val date: String,
    val dateEpoch: Long,
    val maxTemp: Double,
    val minTemp: Double,
    val avgTemp: Double,
    val condition: String,
    val iconUrl: String
)
