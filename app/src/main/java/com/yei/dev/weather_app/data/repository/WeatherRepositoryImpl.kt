package com.yei.dev.weather_app.data.repository

import com.yei.dev.weather_app.data.remote.api.WeatherApiService
import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.mapper.Converter
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val apiKey: String,
    private val locationMapper: Converter<LocationDto, Location>
) : WeatherRepository {
    
    override fun searchLocations(query: String): Flow<Result<List<Location>>> = flow {
        try {
            val response = apiService.searchLocations(
                apiKey = apiKey,
                query = query
            )
            val locations = locationMapper.convertList(response)
            emit(Result.success(locations))
        } catch (e: HttpException) {
            emit(Result.failure(Exception("$MESSAGE_ERROR_SERVER ${e.message()}")))
        } catch (e: IOException) {
            emit(Result.failure(Exception(MESSAGE_ERROR_CONNECTION)))
        } catch (e: Exception) {
            emit(Result.failure(Exception("$MESSAGE_ERROR_UNKNOWN ${e.message}")))
        }
    }

    internal companion object {
        const val MESSAGE_ERROR_SERVER = "Error the server:"
        const val MESSAGE_ERROR_CONNECTION = "Error connection. Check your internet"
        const val MESSAGE_ERROR_UNKNOWN = "Unexpected error"
    }
}
