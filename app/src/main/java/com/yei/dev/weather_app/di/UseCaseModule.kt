package com.yei.dev.weather_app.di

import com.yei.dev.weather_app.domain.repository.WeatherRepository
import com.yei.dev.weather_app.domain.usecase.CalculateAverageTemperatureUseCase
import com.yei.dev.weather_app.domain.usecase.GetWeatherForecastUseCase
import com.yei.dev.weather_app.domain.usecase.SearchLocationsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSearchLocationsUseCase(
        repository: WeatherRepository
    ): SearchLocationsUseCase =
        SearchLocationsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetWeatherForecastUseCase(
        repository: WeatherRepository
    ): GetWeatherForecastUseCase =
        GetWeatherForecastUseCase(repository)

    @Provides
    @Singleton
    fun provideCalculateAverageTemperatureUseCase(): CalculateAverageTemperatureUseCase =
        CalculateAverageTemperatureUseCase()

}
