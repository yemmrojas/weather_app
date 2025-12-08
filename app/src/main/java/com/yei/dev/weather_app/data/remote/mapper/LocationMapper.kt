package com.yei.dev.weather_app.data.remote.mapper

import com.yei.dev.weather_app.data.remote.dto.LocationDto
import com.yei.dev.weather_app.domain.model.Location
import javax.inject.Inject

/**
 * Converter para transformar LocationDto a Location (entidad de dominio)
 */
class LocationMapper @Inject constructor() : Converter<LocationDto, Location> {
    
    override fun convert(input: LocationDto): Location {
        return Location(
            id = input.id,
            name = input.name,
            region = input.region,
            country = input.country,
            lat = input.lat,
            lon = input.lon
        )
    }
}
