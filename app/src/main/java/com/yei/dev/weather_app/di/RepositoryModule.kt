package com.yei.dev.weather_app.di

import com.yei.dev.weather_app.data.remote.api.WeatherApiService
import com.yei.dev.weather_app.data.remote.dto.CurrentWeatherDto
import com.yei.dev.weather_app.data.remote.dto.ForecastDayDto
import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.dto.WeatherForecastResponseDto
import com.yei.dev.weather_app.data.remote.mapper.Converter
import com.yei.dev.weather_app.data.remote.mapper.CurrentWeatherMapper
import com.yei.dev.weather_app.data.remote.mapper.ForecastDayMapper
import com.yei.dev.weather_app.data.remote.mapper.LocationMapper
import com.yei.dev.weather_app.data.remote.mapper.WeatherForecastMapper
import com.yei.dev.weather_app.data.repository.WeatherRepositoryImpl
import com.yei.dev.weather_app.di.qualifier.ApiKey
import com.yei.dev.weather_app.domain.model.CurrentWeather
import com.yei.dev.weather_app.domain.model.ForecastDay
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.model.WeatherForecast
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
    fun provideLocationMapper(): Converter<LocationDto, Location> = LocationMapper()

    @Provides
    @Singleton
    fun provideCurrentWeatherMapper(): Converter<CurrentWeatherDto, CurrentWeather> =
        CurrentWeatherMapper()

    @Provides
    @Singleton
    fun provideForecastDayMapper(): Converter<ForecastDayDto, ForecastDay> =
        ForecastDayMapper()

    @Provides
    @Singleton
    fun provideWeatherForecastMapper(
        locationMapper: Converter<LocationDto, Location>,
        currentWeatherMapper: Converter<CurrentWeatherDto, CurrentWeather>,
        forecastDayMapper: Converter<ForecastDayDto, ForecastDay>
    ): Converter<WeatherForecastResponseDto, WeatherForecast> = WeatherForecastMapper(
        locationMapper = locationMapper,
        currentWeatherMapper = currentWeatherMapper,
        forecastDayMapper = forecastDayMapper
    )

    @Provides
    @Singleton
    fun provideWeatherRepository(
        apiService: WeatherApiService,
        @ApiKey apiKey: String,
        locationMapper: Converter<LocationDto, Location>,
        weatherForecastMapper: Converter<WeatherForecastResponseDto, WeatherForecast>
    ): WeatherRepository = WeatherRepositoryImpl(
        apiService = apiService,
        apiKey = apiKey,
        locationMapper = locationMapper,
        weatherForecastMapper = weatherForecastMapper
    )
}
