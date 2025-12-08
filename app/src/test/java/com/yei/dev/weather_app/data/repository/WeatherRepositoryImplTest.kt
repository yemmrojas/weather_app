package com.yei.dev.weather_app.data.repository

import com.yei.dev.weather_app.data.remote.api.WeatherApiService
import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.mapper.Converter
import com.yei.dev.weather_app.data.repository.WeatherRepositoryImpl.Companion.MESSAGE_ERROR_CONNECTION
import com.yei.dev.weather_app.domain.model.Location
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepositoryImplTest {

    @Test
    fun `searchLocations should return success when API call succeeds`() = runTest {
        // Given
        val apiService = providesApiServiceMock()
        val mapper = providesMapperMock()
        val sut = providesSut(apiService, mapper)
        val query = "Mosquera"

        // When & Then
        sut.searchLocations(query).collect { result ->
            assert(result.isSuccess)
            assert(result.getOrNull()?.size == 3)
            assert(result.getOrNull()?.first()?.name == "Mosquera")
        }
    }

    @Test
    fun `searchLocations should return failure when HttpException occurs`() = runTest {
        // Given
        val apiService = providesApiServiceMock(throwHttpException = true)
        val mapper = providesMapperMock()
        val sut = providesSut(apiService, mapper)
        val query = "Mosquera"

        // When & Then
        sut.searchLocations(query).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message?.contains("Error the server:") == true)
        }
    }

    @Test
    fun `searchLocations should return failure when IOException occurs`() = runTest {
        // Given
        val apiService = providesApiServiceMock(throwIOException = true)
        val mapper = providesMapperMock()
        val sut = providesSut(apiService, mapper)
        val query = "Mosquera"

        // When & Then
        sut.searchLocations(query).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message == MESSAGE_ERROR_CONNECTION)
        }
    }

    @Test
    fun `searchLocations should return failure when unknown exception occurs`() = runTest {
        // Given
        val apiService = providesApiServiceMock(throwUnknownException = true)
        val mapper = providesMapperMock()
        val sut = providesSut(apiService, mapper)
        val query = "Mosquera"

        // When & Then
        sut.searchLocations(query).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message?.contains("Unexpected error") == true)
        }
    }

    @Test
    fun `searchLocations should return empty list when API returns empty list`() = runTest {
        // Given
        val apiService = providesApiServiceMock(returnEmptyList = true)
        val mapper = providesMapperMock()
        val sut = providesSut(apiService, mapper)
        val query = "NonExistentCity"

        // When & Then
        sut.searchLocations(query).collect { result ->
            assert(result.isSuccess)
            assert(result.getOrNull()?.isEmpty() == true)
        }
    }

    @Test
    fun `searchLocations should use mapper to convert DTOs to domain models`() = runTest {
        // Given
        val apiService = providesApiServiceMock()
        val mapper = providesMapperMock()
        val sut = providesSut(apiService, mapper)
        val query = "Mosquera"

        // When & Then
        sut.searchLocations(query).collect { result ->
            assert(result.isSuccess)
            val locations = result.getOrNull()
            assert(locations?.all { it is Location } == true)
        }
    }

    private fun providesSut(
        apiService: WeatherApiService,
        mapper: Converter<LocationDto, Location>,
        apiKey: String = "test_api_key"
    ) = WeatherRepositoryImpl(
        apiService = apiService,
        apiKey = apiKey,
        locationMapper = mapper
    )

    private fun providesApiServiceMock(
        throwHttpException: Boolean = false,
        throwIOException: Boolean = false,
        throwUnknownException: Boolean = false,
        returnEmptyList: Boolean = false
    ) = mockk<WeatherApiService>().apply {
        when {
            throwHttpException -> {
                coEvery { searchLocations(any(), any()) } throws HttpException(
                    Response.error<List<LocationDto>>(
                        500,
                        "Server Error".toResponseBody()
                    )
                )
            }
            throwIOException -> {
                coEvery { searchLocations(any(), any()) } throws IOException("Network error")
            }
            throwUnknownException -> {
                coEvery { searchLocations(any(), any()) } throws RuntimeException("Unknown error")
            }
            returnEmptyList -> {
                coEvery { searchLocations(any(), any()) } returns emptyList()
            }
            else -> {
                coEvery { searchLocations(any(), any()) } returns providesLocationDtoList()
            }
        }
    }

    private fun providesMapperMock() = mockk<Converter<LocationDto, Location>>().apply {
        every { convertList(any()) } answers {
            val dtoList = firstArg<List<LocationDto>>()
            dtoList.map { dto ->
                Location(
                    id = dto.id,
                    name = dto.name,
                    region = dto.region,
                    country = dto.country,
                    lat = dto.lat,
                    lon = dto.lon
                )
            }
        }
    }

    private fun providesLocationDtoList() = listOf(
        LocationDto(
            id = 1L,
            name = "Mosquera",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7059,
            lon = -74.2302,
            url = "mosquera-cundinamarca-colombia"
        ),
        LocationDto(
            id = 2L,
            name = "Funza",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7167,
            lon = -74.2167,
            url = "funza-cundinamarca-colombia"
        ),
        LocationDto(
            id = 3L,
            name = "Madrid",
            region = "Cundinamarca",
            country = "Colombia",
            lat = 4.7333,
            lon = -74.2667,
            url = "madrid-cundinamarca-colombia"
        )
    )
}
