package com.yei.dev.weather_app.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yei.dev.weather_app.domain.usecase.CalculateAverageTemperatureUseCase
import com.yei.dev.weather_app.domain.usecase.GetWeatherForecastUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val calculateAverageTemperatureUseCase: CalculateAverageTemperatureUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherDetailState())
    val state: StateFlow<WeatherDetailState> = _state.asStateFlow()

    private var currentLocation: String = ""

    /**
     * Carga el pronóstico para una ubicación específica
     */
    fun loadWeatherForLocation(location: String) {
        if (location.isEmpty()) return
        currentLocation = location
        _state.update { it.copy(location = location) }
        loadWeatherForecast()
    }

    /**
     * Reintenta cargar el pronóstico del clima
     */
    fun retry() {
        loadWeatherForecast()
    }

    /**
     * Carga el pronóstico del clima para la ubicación actual
     */
    private fun loadWeatherForecast() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = WeatherDetailUiState.Loading) }

            getWeatherForecastUseCase(currentLocation).collect { result ->
                result.fold(
                    onSuccess = { weatherForecast ->
                        val averageTemp = calculateAverageTemperatureUseCase(
                            weatherForecast.forecastDays
                        )

                        _state.update { state ->
                            state.copy(
                                uiState = WeatherDetailUiState.Success(
                                    weatherForecast = weatherForecast,
                                    averageTemperature = averageTemp
                                )
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update { state ->
                            state.copy(
                                uiState = WeatherDetailUiState.Error(
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
        private const val MESSAGE_ERROR_UNKNOWN = "Unknown error occurred"
    }
}
