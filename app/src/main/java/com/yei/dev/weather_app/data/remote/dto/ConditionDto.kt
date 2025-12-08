package com.yei.dev.weather_app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDto(
    @SerialName("text")
    val text: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("code")
    val code: Int
)
