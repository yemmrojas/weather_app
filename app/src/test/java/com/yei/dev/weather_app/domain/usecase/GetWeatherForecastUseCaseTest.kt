package com.yei.dev.weather_app.domain.usecase

import com.yei.dev.weather_app.domain.model.CurrentWeather
import com.yei.dev.weather_app.domain.model.ForecastDay
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.model.WeatherForecast
import com.yei.dev.weather_app.domain.repository.WeatherRepository
import com.yei.dev.weather_app.domain.usecase.GetWeatherForecastUseCase.Companion.MESSAGE_ERROR_EMPTY_LOCATION
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class GetWeatherForecastUseCaseTest {

    @Test
    fun `invoke should return success when repository returns success`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)

        // When & Then
        sut(LOCATION).collect { result ->
            assertTrue(result.isSuccess)
            assertTrue(result.getOrNull()?.location?.name == LOCATION)
            assertTrue(result.getOrNull()?.forecastDays?.size == 3)
        }
    }

    @Test
    fun `invoke should return failure when repository returns failure`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock(isFailure = true)
        val sut = providesSut(repository)

        // When & Then
        sut(LOCATION).collect { result ->
            assert(result.isFailure)
        }
    }

    @Test
    fun `invoke should return failure when location is empty`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)
        val location = ""

        // When & Then
        sut(location).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message == MESSAGE_ERROR_EMPTY_LOCATION)
        }
    }

    @Test
    fun `invoke should return failure when location is blank`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)
        val location = "   "

        // When & Then
        sut(location).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message == MESSAGE_ERROR_EMPTY_LOCATION)
        }
    }

    @Test
    fun `invoke should trim location before calling repository`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)
        val location = "  Mosquera  "

        // When & Then
        sut(location).collect { result ->
            assert(result.isSuccess)
            assert(result.getOrNull()?.location?.name == LOCATION)
        }
    }

    @Test
    fun `invoke should handle location with special characters`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)
        val location = "São Paulo"

        // When & Then
        sut(location).collect { result ->
            assert(result.isSuccess)
        }
    }

    @Test
    fun `invoke should return weather forecast with current weather`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)

        // When & Then
        sut(LOCATION).collect { result ->
            assert(result.isSuccess)
            val forecast = result.getOrNull()
            assert(forecast?.currentWeather != null)
            assert(forecast?.currentWeather?.temperature == 18.5)
        }
    }

    @Test
    fun `invoke should return weather forecast with 3 days`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)

        // When & Then
        sut(LOCATION).collect { result ->
            assert(result.isSuccess)
            val forecast = result.getOrNull()
            assert(forecast?.forecastDays?.size == 3)
        }
    }

    @Test
    fun `invoke should return weather forecast with location details`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)
        val regionExpected = "Cundinamarca"
        val countryExpected = "Colombia"

        // When & Then
        sut(LOCATION).collect { result ->
            assert(result.isSuccess)
            val forecast = result.getOrNull()
            assertEquals(LOCATION, forecast?.location?.name)
            assertEquals(regionExpected, forecast?.location?.region)
            assertEquals(countryExpected, forecast?.location?.country)
        }
    }

    private fun providesWeatherRepositoryMock(isFailure: Boolean = false) =
        mockk<WeatherRepository>().apply {
            if (isFailure) {
                every { getWeatherForecast(any()) } returns flowOf(
                    Result.failure(Exception("Error"))
                )
            } else {
                every { getWeatherForecast(any()) } returns flowOf(
                    Result.success(providesWeatherForecast())
                )
            }
        }

    private fun providesWeatherForecast() = WeatherForecast(
        location = Location(
            id = null,
            name = "Mosquera",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7059,
            lon = -74.2302
        ),
        currentWeather = CurrentWeather(
            temperature = 18.5,
            feelsLike = 17.8,
            condition = "Partly cloudy",
            iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
        ),
        forecastDays = listOf(
            ForecastDay(
                date = "2024-12-08",
                dateEpoch = 1733616000L,
                maxTemp = 22.0,
                minTemp = 15.0,
                avgTemp = 18.5,
                condition = "Partly cloudy",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
            ),
            ForecastDay(
                date = "2024-12-09",
                dateEpoch = 1733702400L,
                maxTemp = 23.0,
                minTemp = 16.0,
                avgTemp = 19.5,
                condition = "Sunny",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/113.png"
            ),
            ForecastDay(
                date = "2024-12-10",
                dateEpoch = 1733788800L,
                maxTemp = 21.0,
                minTemp = 14.0,
                avgTemp = 17.5,
                condition = "Cloudy",
                iconUrl = "https://cdn.weatherapi.com/weather/64x64/day/119.png"
            )
        )
    )

    private fun providesSut(repository: WeatherRepository) = GetWeatherForecastUseCase(repository)

    companion object {
        private const val LOCATION = "Mosquera"
    }
}
