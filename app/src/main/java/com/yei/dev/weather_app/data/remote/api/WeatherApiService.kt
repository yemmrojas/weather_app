package com.yei.dev.weather_app.data.remote.api

import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.dto.WeatherForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    
    @GET("search.json")
    suspend fun searchLocations(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<LocationDto>
    
    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 3
    ): WeatherForecastResponseDto
}
