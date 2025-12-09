package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.CurrentWeatherDto
import com.yei.dev.weather_app.data.remote.dto.ForecastDayDto
import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.dto.WeatherForecastResponseDto
import com.yei.dev.weather_app.domain.model.CurrentWeather
import com.yei.dev.weather_app.domain.model.ForecastDay
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.model.WeatherForecast

class WeatherForecastMapper(
    private val locationMapper: Converter<LocationDto, Location>,
    private val currentWeatherMapper: Converter<CurrentWeatherDto, CurrentWeather>,
    private val forecastDayMapper: Converter<ForecastDayDto, ForecastDay>
) : Converter<WeatherForecastResponseDto, WeatherForecast> {
    
    override fun convert(input: WeatherForecastResponseDto): WeatherForecast {
        return WeatherForecast(
            location = locationMapper.convert(input.location),
            currentWeather = currentWeatherMapper.convert(input.current),
            forecastDays = forecastDayMapper.convertList(input.forecast.forecastDay)
        )
    }
}
