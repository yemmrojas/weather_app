package com.yei.dev.weather_app.presentation.search

import com.yei.dev.weather_app.domain.model.Location

/**
 * Estados de UI para la pantalla de búsqueda de ubicaciones
 */
sealed class SearchUiState {
    
    /**
     * Estado inicial - No se ha realizado ninguna búsqueda
     */
    data object Idle : SearchUiState()
    
    /**
     * Estado de carga - Se está realizando la búsqueda
     */
    data object Loading : SearchUiState()
    
    /**
     * Estado de éxito - Se encontraron resultados
     * @param locations Lista de ubicaciones encontradas
     */
    data class Success(val locations: List<Location>) : SearchUiState()
    
    /**
     * Estado vacío - La búsqueda no retornó resultados
     * @param query Texto de búsqueda que no produjo resultados
     */
    data class Empty(val query: String) : SearchUiState()
    
    /**
     * Estado de error - Ocurrió un error durante la búsqueda
     * @param message Mensaje descriptivo del error
     */
    data class Error(val message: String) : SearchUiState()
}
