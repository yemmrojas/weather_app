package com.yei.dev.weather_app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yei.dev.weather_app.domain.usecase.SearchLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val searchLocationsUseCase: SearchLocationsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(WeatherSearchState())
    val state: StateFlow<WeatherSearchState> = _state.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    
    init {
        observeSearchQuery()
    }
    
    /**
     * Observa cambios en el query de búsqueda con debounce
     */
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(DEBOUNCE_DELAY_MS)
            .distinctUntilChanged()
            .filter { it.trim().length >= MIN_QUERY_LENGTH || it.isEmpty() }
            .onEach { query ->
                if (query.isEmpty()) {
                    _state.update { it.copy(uiState = SearchUiState.Idle) }
                } else {
                    searchLocations(query)
                }
            }
            .launchIn(viewModelScope)
    }
    
    /**
     * Maneja eventos de UI
     */
    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                _searchQuery.value = event.query
            }
            
            is SearchEvent.OnLocationSelected -> {
                // TODO: Navegar a pantalla de detalle
                // Por ahora solo log
            }
            
            SearchEvent.OnClearSearch -> {
                _state.update { 
                    WeatherSearchState(
                        searchQuery = "",
                        uiState = SearchUiState.Idle
                    )
                }
                _searchQuery.value = ""
            }
            
            SearchEvent.OnRetry -> {
                val currentQuery = _state.value.searchQuery
                if (currentQuery.isNotEmpty()) {
                    searchLocations(currentQuery)
                }
            }
        }
    }
    
    /**
     * Realiza la búsqueda de ubicaciones
     */
    private fun searchLocations(query: String) {
        viewModelScope.launch {

            _state.update { it.copy(uiState = SearchUiState.Loading) }

            searchLocationsUseCase(query).collect { result ->
                result.fold(
                    onSuccess = { locations ->
                        _state.update { state ->
                            state.copy(
                                uiState = if (locations.isEmpty()) {
                                    SearchUiState.Empty(query)
                                } else {
                                    SearchUiState.Success(locations)
                                }
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update { state ->
                            state.copy(
                                uiState = SearchUiState.Error(
                                    error.message ?: MESSAGE_ERROR_UNKNOWN
                                )
                            )
                        }
                    }
                )
            }
        }
    }
    
    companion object {
        private const val DEBOUNCE_DELAY_MS = 500L
        private const val MIN_QUERY_LENGTH = 3
        private const val MESSAGE_ERROR_UNKNOWN = "Unknown error occurred"
    }
}
