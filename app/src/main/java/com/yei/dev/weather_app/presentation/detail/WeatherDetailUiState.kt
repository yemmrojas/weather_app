package com.yei.dev.weather_app.presentation.detail

import com.yei.dev.weather_app.domain.model.WeatherForecast

sealed class WeatherDetailUiState {
    data object Loading : WeatherDetailUiState()
    
    data class Success(
        val weatherForecast: WeatherForecast,
        val averageTemperature: Double
    ) : WeatherDetailUiState()
    
    data class Error(val message: String) : WeatherDetailUiState()
}
