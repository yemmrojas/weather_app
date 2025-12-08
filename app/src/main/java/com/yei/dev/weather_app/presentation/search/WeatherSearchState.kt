package com.yei.dev.weather_app.presentation.search

/**
 * Estado completo de la pantalla de búsqueda
 * 
 * @param searchQuery Texto actual en el campo de búsqueda
 * @param uiState Estado de la UI (Loading, Success, Error, etc.)
 */
data class WeatherSearchState(
    val searchQuery: String = "",
    val uiState: SearchUiState = SearchUiState.Idle
)
