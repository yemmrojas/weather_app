package com.yei.dev.weather_app.presentation.detail

import com.yei.dev.weather_app.domain.model.CurrentWeather
import com.yei.dev.weather_app.domain.model.ForecastDay
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.model.WeatherForecast
import com.yei.dev.weather_app.domain.usecase.CalculateAverageTemperatureUseCase
import com.yei.dev.weather_app.domain.usecase.GetWeatherForecastUseCase
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading with empty location`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock()
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)

        // When & Then
        val state = sut.state.value
        assertEquals("", state.location)
        assertTrue(state.uiState is WeatherDetailUiState.Loading)
    }

    @Test
    fun `loadWeatherForLocation should update location in state`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock()
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)

        // When
        sut.loadWeatherForLocation(LOCATION)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertEquals(LOCATION, state.location)
    }

    @Test
    fun `loadWeatherForLocation should set Loading state initially`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock()
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)
        // When
        sut.loadWeatherForLocation(LOCATION)

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is WeatherDetailUiState.Loading)
    }

    @Test
    fun `loadWeatherForLocation should set Success state when use case succeeds`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock()
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)

        // When
        sut.loadWeatherForLocation(LOCATION)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is WeatherDetailUiState.Success)
        val successState = state.uiState as WeatherDetailUiState.Success
        assertEquals(LOCATION, successState.weatherForecast.location.name)
        assertEquals(3, successState.weatherForecast.forecastDays.size)
    }

    @Test
    fun `loadWeatherForLocation should calculate average temperature`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock()
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)

        // When
        sut.loadWeatherForLocation(LOCATION)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is WeatherDetailUiState.Success)
        val successState = state.uiState as WeatherDetailUiState.Success
        assertEquals(22.0, successState.averageTemperature, 0.01)
    }

    @Test
    fun `loadWeatherForLocation should set Error state when use case fails`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock(isFailure = true)
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)

        // When
        sut.loadWeatherForLocation(LOCATION)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is WeatherDetailUiState.Error)
        val errorState = state.uiState as WeatherDetailUiState.Error
        assertEquals("Network error", errorState.message)
    }

    @Test
    fun `loadWeatherForLocation should not load when location is empty`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock()
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)
        val location = ""

        // When
        sut.loadWeatherForLocation(location)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertEquals("", state.location)
        assertTrue(state.uiState is WeatherDetailUiState.Loading)
    }

    @Test
    fun `retry should reload weather forecast`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock()
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)
        val location = "Mosquera"

        // When
        sut.loadWeatherForLocation(location)
        advanceUntilIdle()
        sut.retry()
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is WeatherDetailUiState.Success)
    }

    @Test
    fun `loadWeatherForLocation should handle error with unknown message`() = runTest {
        // Given
        val getWeatherForecastUseCase = providesGetWeatherForecastUseCaseMock(
            isFailure = true,
            errorMessage = null
        )
        val calculateAverageTemperatureUseCase = providesCalculateAverageTemperatureUseCaseMock()
        val sut = providesSut(getWeatherForecastUseCase, calculateAverageTemperatureUseCase)

        // When
        sut.loadWeatherForLocation(LOCATION)
        advanceUntilIdle()

        // Then
        val state = sut.state.value
        assertTrue(state.uiState is WeatherDetailUiState.Error)
        val errorState = state.uiState as WeatherDetailUiState.Error
        assertEquals("Unknown error occurred", errorState.message)
    }

    private fun providesGetWeatherForecastUseCaseMock(
        isFailure: Boolean = false,
        errorMessage: String? = "Network error"
    ) = mockk<GetWeatherForecastUseCase>().apply {
        if (isFailure) {
            every { this@apply(any()) } returns flowOf(
                Result.failure(Exception(errorMessage))
            )
        } else {
            every { this@apply(any()) } returns flowOf(
                Result.success(providesWeatherForecast())
            )
        }
    }

    private fun providesCalculateAverageTemperatureUseCaseMock() =
        mockk<CalculateAverageTemperatureUseCase>().apply {
            every { this@apply(any()) } returns 22.0
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

    private fun providesSut(
        getWeatherForecastUseCase: GetWeatherForecastUseCase,
        calculateAverageTemperatureUseCase: CalculateAverageTemperatureUseCase
    ) = WeatherDetailViewModel(
        getWeatherForecastUseCase = getWeatherForecastUseCase,
        calculateAverageTemperatureUseCase = calculateAverageTemperatureUseCase
    )

    companion object {
        private const val LOCATION = "Mosquera"
    }
}
