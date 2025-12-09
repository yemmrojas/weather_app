package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.CurrentWeatherDto
import com.yei.dev.weather_app.domain.model.CurrentWeather

class CurrentWeatherMapper : Converter<CurrentWeatherDto, CurrentWeather> {
    override fun convert(input: CurrentWeatherDto): CurrentWeather {
        return CurrentWeather(
            temperature = input.tempC,
            feelsLike = input.feelsLikeC,
            condition = input.condition.text,
            iconUrl = "https:${input.condition.icon}"
        )
    }
}
