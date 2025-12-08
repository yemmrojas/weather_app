package com.yei.dev.weather_app.domain.repository

import com.yei.dev.weather_app.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    
    fun searchLocations(query: String): Flow<Result<List<Location>>>
}
