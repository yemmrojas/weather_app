package com.yei.dev.weather_app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastDayDto(
    @SerialName("date")
    val date: String,
    @SerialName("date_epoch")
    val dateEpoch: Long,
    @SerialName("day")
    val day: DayDto
)

@Serializable
data class DayDto(
    @SerialName("maxtemp_c")
    val maxTempC: Double,
    @SerialName("mintemp_c")
    val minTempC: Double,
    @SerialName("avgtemp_c")
    val avgTempC: Double,
    @SerialName("condition")
    val condition: ConditionDto
)
