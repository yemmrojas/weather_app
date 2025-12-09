package com.yei.dev.weather_app.domain.usecase

import com.yei.dev.weather_app.domain.model.WeatherForecast
import com.yei.dev.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWeatherForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    
    operator fun invoke(location: String): Flow<Result<WeatherForecast>> = flow {
        if (location.trim().isEmpty()) {
            emit(Result.failure(Exception(MESSAGE_ERROR_EMPTY_LOCATION)))
            return@flow
        }
        
        repository.getWeatherForecast(location.trim()).collect { result ->
            emit(result)
        }
    }
    
    internal companion object {
        const val MESSAGE_ERROR_EMPTY_LOCATION = "Location cannot be empty"
    }
}
