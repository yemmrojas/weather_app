package com.yei.dev.weather_app.domain.model

data class Location(
    val id: Long? = null,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double
)
