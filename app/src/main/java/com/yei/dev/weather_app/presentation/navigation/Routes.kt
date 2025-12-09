package com.yei.dev.weather_app.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


sealed class Routes: NavKey {
    @Serializable
    data object Splash: Routes()

    @Serializable
    data object WeatherSearch: Routes()
    
    @Serializable
    data class WeatherDetail(val location: String): Routes()

    @Serializable
    data object Error: Routes()
}
