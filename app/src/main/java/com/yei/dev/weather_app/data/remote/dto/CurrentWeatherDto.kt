package com.yei.dev.weather_app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherDto(
    @SerialName("temp_c")
    val tempC: Double,
    @SerialName("condition")
    val condition: ConditionDto,
    @SerialName("feelslike_c")
    val feelsLikeC: Double
)
