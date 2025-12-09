package com.yei.dev.weather_app.presentation.detail

data class WeatherDetailState(
    val location: String = "",
    val uiState: WeatherDetailUiState = WeatherDetailUiState.Loading
)
