package com.yei.dev.weather_app.data.repository

import com.yei.dev.weather_app.data.remote.api.WeatherApiService
import com.yei.dev.weather_app.data.remote.dto.ConditionDto
import com.yei.dev.weather_app.data.remote.dto.CurrentWeatherDto
import com.yei.dev.weather_app.data.remote.dto.DayDto
import com.yei.dev.weather_app.data.remote.dto.ForecastDayDto
import com.yei.dev.weather_app.data.remote.dto.ForecastDto
import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.data.remote.dto.WeatherForecastResponseDto
import com.yei.dev.weather_app.data.remote.mapper.Converter
import com.yei.dev.weather_app.data.repository.WeatherRepositoryImpl.Companion.MESSAGE_ERROR_CONNECTION
import com.yei.dev.weather_app.domain.model.CurrentWeather
import com.yei.dev.weather_app.domain.model.ForecastDay
import com.yei.dev.weather_app.domain.model.Location
import com.yei.dev.weather_app.domain.model.WeatherForecast
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepositoryImplTest {

    @Test
    fun `searchLocations should return success when API call succeeds`() = runTest {
        // Given
        val apiService = providesApiServiceMock()
        val locationMapper = providesMapperMock()
        val sut = providesSut(apiService, locationMapper)
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
        val locationMapper = providesMapperMock()
        val sut = providesSut(apiService, locationMapper)
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
        val locationMapper = providesMapperMock()
        val sut = providesSut(apiService, locationMapper)
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
        val locationMapper = providesMapperMock()
        val sut = providesSut(apiService, locationMapper)
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
        val locationMapper = providesMapperMock()
        val sut = providesSut(apiService, locationMapper)
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
        val locationMapper = providesMapperMock()
        val sut = providesSut(apiService, locationMapper)
        val query = "Mosquera"

        // When & Then
        sut.searchLocations(query).collect { result ->
            assert(result.isSuccess)
            val locations = result.getOrNull()
            assertTrue(locations?.all { it is Location } == true)
        }
    }

    private fun providesSut(
        apiService: WeatherApiService,
        locationMapper: Converter<LocationDto, Location>,
        weatherForecastMapper: Converter<WeatherForecastResponseDto, WeatherForecast>? = null,
        apiKey: String = "test_api_key"
    ) = WeatherRepositoryImpl(
        apiService = apiService,
        apiKey = apiKey,
        locationMapper = locationMapper,
        weatherForecastMapper = weatherForecastMapper ?: providesWeatherForecastMapperMock()
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

    // ==================== Weather Forecast Tests ====================

    @Test
    fun `getWeatherForecast should return success when API call succeeds`() = runTest {
        // Given
        val apiService = providesWeatherForecastApiServiceMock()
        val locationMapper = providesMapperMock()
        val weatherForecastMapper = providesWeatherForecastMapperMock()
        val sut = providesSut(apiService, locationMapper, weatherForecastMapper)
        val location = "Mosquera"

        // When & Then
        sut.getWeatherForecast(location).collect { result ->
            assert(result.isSuccess)
            val forecast = result.getOrNull()
            assert(forecast != null)
            assert(forecast?.location?.name == "Mosquera")
            assert(forecast?.forecastDays?.size == 3)
        }
    }

    @Test
    fun `getWeatherForecast should return failure when HttpException occurs`() = runTest {
        // Given
        val apiService = providesWeatherForecastApiServiceMock(throwHttpException = true)
        val locationMapper = providesMapperMock()
        val weatherForecastMapper = providesWeatherForecastMapperMock()
        val sut = providesSut(apiService, locationMapper, weatherForecastMapper)
        val location = "Mosquera"

        // When & Then
        sut.getWeatherForecast(location).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message?.contains("Error the server:") == true)
        }
    }

    @Test
    fun `getWeatherForecast should return failure when IOException occurs`() = runTest {
        // Given
        val apiService = providesWeatherForecastApiServiceMock(throwIOException = true)
        val locationMapper = providesMapperMock()
        val weatherForecastMapper = providesWeatherForecastMapperMock()
        val sut = providesSut(apiService, locationMapper, weatherForecastMapper)
        val location = "Mosquera"

        // When & Then
        sut.getWeatherForecast(location).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message == MESSAGE_ERROR_CONNECTION)
        }
    }

    @Test
    fun `getWeatherForecast should return failure when unknown exception occurs`() = runTest {
        // Given
        val apiService = providesWeatherForecastApiServiceMock(throwUnknownException = true)
        val locationMapper = providesMapperMock()
        val weatherForecastMapper = providesWeatherForecastMapperMock()
        val sut = providesSut(apiService, locationMapper, weatherForecastMapper)
        val location = "Mosquera"

        // When & Then
        sut.getWeatherForecast(location).collect { result ->
            assert(result.isFailure)
            assert(result.exceptionOrNull()?.message?.contains("Unexpected error") == true)
        }
    }

    @Test
    fun `getWeatherForecast should use mapper to convert DTO to domain model`() = runTest {
        // Given
        val apiService = providesWeatherForecastApiServiceMock()
        val locationMapper = providesMapperMock()
        val weatherForecastMapper = providesWeatherForecastMapperMock()
        val sut = providesSut(apiService, locationMapper, weatherForecastMapper)
        val location = "Mosquera"

        // When & Then
        sut.getWeatherForecast(location).collect { result ->
            assert(result.isSuccess)
            val forecast = result.getOrNull()
            assert(forecast is WeatherForecast)
            assert(forecast?.currentWeather is CurrentWeather)
            assert(forecast?.forecastDays?.all { it is ForecastDay } == true)
        }
    }

    @Test
    fun `getWeatherForecast should request 3 days forecast`() = runTest {
        // Given
        val apiService = providesWeatherForecastApiServiceMock()
        val locationMapper = providesMapperMock()
        val weatherForecastMapper = providesWeatherForecastMapperMock()
        val sut = providesSut(apiService, locationMapper, weatherForecastMapper)
        val location = "Mosquera"

        // When & Then
        sut.getWeatherForecast(location).collect { result ->
            assert(result.isSuccess)
            val forecast = result.getOrNull()
            assert(forecast?.forecastDays?.size == 3)
        }
    }

    private fun providesWeatherForecastApiServiceMock(
        throwHttpException: Boolean = false,
        throwIOException: Boolean = false,
        throwUnknownException: Boolean = false
    ) = mockk<WeatherApiService>().apply {
        coEvery { searchLocations(any(), any()) } returns providesLocationDtoList()

        when {
            throwHttpException -> {
                coEvery { getWeatherForecast(any(), any(), any()) } throws HttpException(
                    Response.error<WeatherForecastResponseDto>(
                        500,
                        "Server Error".toResponseBody()
                    )
                )
            }

            throwIOException -> {
                coEvery {
                    getWeatherForecast(
                        any(),
                        any(),
                        any()
                    )
                } throws IOException("Network error")
            }

            throwUnknownException -> {
                coEvery {
                    getWeatherForecast(
                        any(),
                        any(),
                        any()
                    )
                } throws RuntimeException("Unknown error")
            }

            else -> {
                coEvery {
                    getWeatherForecast(
                        any(),
                        any(),
                        any()
                    )
                } returns providesWeatherForecastResponseDto()
            }
        }
    }

    private fun providesWeatherForecastMapperMock() =
        mockk<Converter<WeatherForecastResponseDto, WeatherForecast>>().apply {
            every { convert(any()) } answers {
                val dto = firstArg<WeatherForecastResponseDto>()
                WeatherForecast(
                    location = Location(
                        id = dto.location.id,
                        name = dto.location.name,
                        region = dto.location.region,
                        country = dto.location.country,
                        lat = dto.location.lat,
                        lon = dto.location.lon
                    ),
                    currentWeather = CurrentWeather(
                        temperature = dto.current.tempC,
                        condition = dto.current.condition.text,
                        iconUrl = "https:${dto.current.condition.icon}",
                        feelsLike = dto.current.feelsLikeC
                    ),
                    forecastDays = dto.forecast.forecastDay.map { forecastDayDto ->
                        ForecastDay(
                            date = forecastDayDto.date,
                            maxTemp = forecastDayDto.day.maxTempC,
                            minTemp = forecastDayDto.day.minTempC,
                            avgTemp = forecastDayDto.day.avgTempC,
                            condition = forecastDayDto.day.condition.text,
                            iconUrl = "https:${forecastDayDto.day.condition.icon}",
                            dateEpoch = forecastDayDto.dateEpoch
                        )
                    }
                )
            }
        }

    private fun providesWeatherForecastResponseDto() =
        WeatherForecastResponseDto(
            location = LocationDto(
                id = null,
                name = "Mosquera",
                region = "Cundinamarca",
                country = "Colombia",
                lat = 4.7059,
                lon = -74.2302,
                url = null
            ),
            current = CurrentWeatherDto(
                tempC = 18.5,
                condition = ConditionDto(
                    text = "Partly cloudy",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                    code = 1003
                ),
                feelsLikeC = 17.8
            ),
            forecast = ForecastDto(
                forecastDay = listOf(
                    ForecastDayDto(
                        date = "2024-12-08",
                        dateEpoch = 1733616000,
                        day = DayDto(
                            maxTempC = 22.0,
                            minTempC = 15.0,
                            avgTempC = 18.5,
                            condition = ConditionDto(
                                text = "Partly cloudy",
                                icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                                code = 1003
                            )
                        )
                    ),
                    ForecastDayDto(
                        date = "2024-12-09",
                        dateEpoch = 1733702400,
                        day = DayDto(
                            maxTempC = 23.0,
                            minTempC = 16.0,
                            avgTempC = 19.5,
                            condition = ConditionDto(
                                text = "Sunny",
                                icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                                code = 1000
                            )
                        )
                    ),
                    ForecastDayDto(
                        date = "2024-12-10",
                        dateEpoch = 1733788800,
                        day = DayDto(
                            maxTempC = 21.0,
                            minTempC = 14.0,
                            avgTempC = 17.5,
                            condition = ConditionDto(
                                text = "Cloudy",
                                icon = "//cdn.weatherapi.com/weather/64x64/day/119.png",
                                code = 1006
                            )
                        )
                    )
                )
            )
        )
}
