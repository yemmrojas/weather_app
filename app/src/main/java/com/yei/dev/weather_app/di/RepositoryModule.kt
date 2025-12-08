package com.yei.dev.weather_app.di

import com.yei.dev.weather_app.data.remote.api.WeatherApiService
import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.mapper.Converter
import com.yei.dev.weather_app.data.remote.mapper.LocationMapper
import com.yei.dev.weather_app.data.repository.WeatherRepositoryImpl
import com.yei.dev.weather_app.di.qualifier.ApiKey
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideLocationMapper(): Converter<LocationDto, Location> {
        return LocationMapper()
    }
    
    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        @ApiKey apiKey: String,
        locationMapper: Converter<LocationDto, Location>
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            apiService = apiService,
            apiKey = apiKey,
            locationMapper = locationMapper
        )
    }
}
