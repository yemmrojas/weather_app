package com.yei.dev.weather_app.domain.usecase

import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.repository.WeatherRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchLocationsUseCaseTest {

    @Test
    fun `invoke should return success when repository returns success`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)
        val query = "Mosquera"
        // When & Then
        sut(query).collect { result ->
            assert(result.isSuccess)
            assert(result.getOrNull()?.size == 3)
        }
    }

    @Test
    fun `invoke should return failure when repository returns failure`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock(isFailure = true)
        val sut = providesSut(repository)
        val query = "Mosquera"
        // When & Then
        sut(query).collect { result ->
            assert(result.isFailure)
        }
    }

    @Test
    fun `invoke should return empty list when query is less than 3 characters`() = runTest {
        // Given
        val repository = providesWeatherRepositoryMock()
        val sut = providesSut(repository)
        val query = "Mo"
        // When & Then
        sut(query).collect { result ->
            assert(result.isSuccess)
            assert(result.getOrNull()?.isEmpty() == true)
        }
    }

    private fun providesWeatherRepositoryMock(isFailure: Boolean = false) =
        mockk<WeatherRepository>().apply {
            if (isFailure) {
                every { searchLocations(any()) } returns flowOf(Result.failure(Exception("Error")))
            } else {
                every { searchLocations(any()) } returns flowOf(Result.success(listLocations()))
            }
        }

    private fun listLocations() = listOf(
        Location(
            id = 1,
            name = "Mosquera",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 0.0,
            lon = 0.0
        ),
        Location(
            id = 2,
            name = "Funza",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 0.0,
            lon = 0.0
        ),
        Location(
            id = 3,
            name = "San Martín de Loba",
            region = "Bolivar",
            country = "Colombia",
            lat = 0.0,
            lon = 0.0
        )
    )

    private fun providesSut(repository: WeatherRepository) = SearchLocationsUseCase(repository)
}
