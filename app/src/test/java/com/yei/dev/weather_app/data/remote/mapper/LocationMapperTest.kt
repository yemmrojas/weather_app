package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.LocationDto
import org.junit.Test

class LocationMapperTest {

    @Test
    fun `convert should map LocationDto to Location correctly`() {
        // Given
        val sut = providesSut()
        val dto = providesLocationDto()
        // When
        val result = sut.convert(dto)
        // Then
        assert(result.id == 1L)
        assert(result.name == "Mosquera")
        assert(result.region == "Cundinamarca")
        assert(result.country == "Colombia")
        assert(result.lat == 4.7059)
        assert(result.lon == -74.2302)
    }

    @Test
    fun `convert should handle empty region correctly`() {
        // Given
        val sut = providesSut()
        val dto = providesLocationDto(region = "")
        // When
        val result = sut.convert(dto)
        // Then
        assert(result.region == "")
        assert(result.name == "Mosquera")
    }

    @Test
    fun `convertList should map list of LocationDto to list of Location`() {
        // Given
        val sut = providesSut()
        val dtoList = providesLocationDtoList()
        // When
        val result = sut.convertList(dtoList)
        // Then
        assert(result.size == 3)
        assert(result[0].name == "Mosquera")
        assert(result[1].name == "Funza")
        assert(result[2].name == "Madrid")
    }

    @Test
    fun `convertList should return empty list when input is empty`() {
        // Given
        val sut = providesSut()
        val emptyList = emptyList<LocationDto>()
        // When
        val result = sut.convertList(emptyList)
        // Then
        assert(result.isEmpty())
    }

    @Test
    fun `convert should preserve latitude and longitude values`() {
        // Given
        val sut = providesSut()
        val dto = providesLocationDto(lat = 51.5074, lon = -0.1278)
        // When
        val result = sut.convert(dto)
        // Then
        assert(result.lat == 51.5074)
        assert(result.lon == -0.1278)
    }

    @Test
    fun `convert should handle negative coordinates correctly`() {
        // Given
        val sut = providesSut()
        val dto = providesLocationDto(lat = -33.8688, lon = 151.2093)
        // When
        val result = sut.convert(dto)
        // Then
        assert(result.lat == -33.8688)
        assert(result.lon == 151.2093)
    }

    private fun providesSut() = LocationMapper()

    private fun providesLocationDto(
        id: Long = 1L,
        name: String = "Mosquera",
        region: String = "Cundinamarca",
        country: String = "Colombia",
        lat: Double = 4.7059,
        lon: Double = -74.2302,
        url: String = "mosquera-cundinamarca-colombia"
    ) = LocationDto(
        id = id,
        name = name,
        region = region,
        country = country,
        lat = lat,
        lon = lon,
        url = url
    )

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
