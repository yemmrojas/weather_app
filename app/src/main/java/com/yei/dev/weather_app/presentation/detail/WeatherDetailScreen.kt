package com.yei.dev.weather_app.presentation.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yei.dev.weather_app.R
import com.yei.dev.weather_app.domain.model.CurrentWeather
import com.yei.dev.weather_app.domain.model.ForecastDay
import com.yei.dev.weather_app.domain.model.WeatherForecast
import com.yei.dev.weather_app.presentation.components.ProgressCircleScreen
import com.yei.dev.weather_app.presentation.components.ShieldHandlerScreen
import com.yei.dev.weather_app.presentation.topBar.WeatherTopBar
import com.yei.dev.weather_app.ui.theme.Purple40
import com.yei.dev.weather_app.ui.theme.Purple700
import com.yei.dev.weather_app.ui.theme.Purple80
import com.yei.dev.weather_app.util.toShortDateFormat

@Composable
fun WeatherDetailScreen(
    location: String,
    viewModel: WeatherDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(location) {
        viewModel.loadWeatherForLocation(location)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple700)
    ) {
        WeatherTopBar(
            title = location,
            onBackClick = onBackClick
        )

        when (val uiState = state.uiState) {
            is WeatherDetailUiState.Loading -> {
                LoadingState()
            }

            is WeatherDetailUiState.Success -> {
                SuccessState(
                    weatherForecast = uiState.weatherForecast,
                    averageTemperature = uiState.averageTemperature
                )
            }

            is WeatherDetailUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = viewModel::retry
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    ProgressCircleScreen(
        description = stringResource(R.string.detail_screen_loading_description_loading_weather),
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    ShieldHandlerScreen(
        icon = Icons.Default.Info,
        title = stringResource(R.string.detail_screen_error_title),
        description = message,
        textButton = stringResource(R.string.text_button_retry),
    ) {
        onRetry()
    }
}

@Composable
private fun SuccessState(
    weatherForecast: WeatherForecast,
    averageTemperature: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CurrentWeatherSection(currentWeather = weatherForecast.currentWeather)

        Spacer(modifier = Modifier.height(24.dp))

        ForecastSection(forecastDays = weatherForecast.forecastDays)

        Spacer(modifier = Modifier.height(24.dp))

        AverageTemperatureSection(averageTemperature = averageTemperature)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CurrentWeatherSection(
    currentWeather: CurrentWeather
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = cardColors(
            containerColor = Purple40
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    R.string.detail_screen_section_now_text_now
                ),
                color = Purple80,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            coil.compose.AsyncImage(
                model = currentWeather.iconUrl,
                contentDescription = currentWeather.condition,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentWeather.condition,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${currentWeather.temperature.toInt()}°C",
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.detail_screen_section_now_text_feels_like_heat,
                    "${currentWeather.feelsLike.toInt()}°C"
                ),
                color = Purple80.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ForecastSection(
    forecastDays: List<ForecastDay>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Pronóstico de 3 días",
            color = Purple80,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        forecastDays.forEachIndexed { index, forecastDay ->
            ForecastDayItem(
                forecastDay = forecastDay,
                isToday = index == 0
            )

            if (index < forecastDays.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ForecastDayItem(
    forecastDay: ForecastDay,
    isToday: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = cardColors(
            containerColor = if (isToday) Purple40 else Purple40.copy(alpha = 0.7f)
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = forecastDay.date.toShortDateFormat(),
                    color = if (isToday) Color.White else Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                )
                if (isToday) {
                    Text(
                        text = stringResource(
                            R.string.detail_screen_section_forecast_text_today
                        ),
                        color = Purple80,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            coil.compose.AsyncImage(
                model = forecastDay.iconUrl,
                contentDescription = forecastDay.condition,
                modifier = Modifier.size(48.dp)
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${forecastDay.maxTemp.toInt()}°C",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${forecastDay.minTemp.toInt()}°C",
                    color = Purple80.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun AverageTemperatureSection(
    averageTemperature: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = cardColors(
            containerColor = Purple80
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    R.string.detail_screen_section_average_temperature_text_average_temperature
                ),
                color = Purple700,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = String.format("%.1f°C", averageTemperature),
                color = Purple700,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
