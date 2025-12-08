package com.yei.dev.weather_app.domain.usecase

import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchLocationsUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    
    operator fun invoke(query: String): Flow<Result<List<Location>>> = flow {

        if (query.trim().length < MIN_QUERY_LENGTH) {
            emit(Result.success(emptyList()))
            return@flow
        }

        repository.searchLocations(query.trim()).collect { result ->
            emit(result)
        }
    }

    internal companion object {
        private const val MIN_QUERY_LENGTH = 3
    }
}
