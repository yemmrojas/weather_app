package com.yei.dev.weather_app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    @SerialName("id")
    val id: Long,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("region")
    val region: String,
    
    @SerialName("country")
    val country: String,
    
    @SerialName("lat")
    val lat: Double,
    
    @SerialName("lon")
    val lon: Double,
    
    @SerialName("url")
    val url: String
)
