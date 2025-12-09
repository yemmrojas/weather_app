package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.ForecastDayDto
import com.yei.dev.weather_app.domain.model.ForecastDay

class ForecastDayMapper : Converter<ForecastDayDto, ForecastDay> {
    override fun convert(input: ForecastDayDto): ForecastDay {
        return ForecastDay(
            date = input.date,
            dateEpoch = input.dateEpoch,
            maxTemp = input.day.maxTempC,
            minTemp = input.day.minTempC,
            avgTemp = input.day.avgTempC,
            condition = input.day.condition.text,
            iconUrl = "https:${input.day.condition.icon}"
        )
    }
}
