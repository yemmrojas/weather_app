package com.yei.dev.weather_app.presentation.search

import com.yei.dev.weather_app.domain.model.Location

/**
 * Eventos de UI para la pantalla de búsqueda
 */
sealed class SearchEvent {
    
    /**
     * El usuario cambió el texto de búsqueda
     * @param query Nuevo texto de búsqueda
     */
    data class OnSearchQueryChanged(val query: String) : SearchEvent()
    
    /**
     * El usuario seleccionó una ubicación de los resultados
     * @param location Ubicación seleccionada
     */
    data class OnLocationSelected(val location: Location) : SearchEvent()
    
    /**
     * El usuario quiere limpiar la búsqueda
     */
    data object OnClearSearch : SearchEvent()
    
    /**
     * El usuario quiere reintentar después de un error
     */
    data object OnRetry : SearchEvent()
}
