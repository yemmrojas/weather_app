package com.yei.dev.weather_app.domain.usecase

import com.yei.dev.weather_app.domain.model.ForecastDay
import javax.inject.Inject
import kotlin.math.round

class CalculateAverageTemperatureUseCase @Inject constructor() {
    
    /**
     * Calcula el promedio de las temperaturas máximas de los días proporcionados
     * @param forecastDays Lista de días del pronóstico
     * @return Temperatura promedio con 1 decimal, o 0.0 si la lista está vacía
     */
    operator fun invoke(forecastDays: List<ForecastDay>): Double {
        if (forecastDays.isNotEmpty()) {
            val sum = forecastDays.sumOf { it.maxTemp }
            val average = sum / forecastDays.size
            return round(average * 10) / 10
        }
        return 0.0
    }
}
