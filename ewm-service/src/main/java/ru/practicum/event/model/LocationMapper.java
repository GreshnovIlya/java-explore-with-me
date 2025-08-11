package ru.practicum.event.model;

import ru.practicum.event.dto.LocationDto;

public class LocationMapper {
    public static Location toLocation(Long id, LocationDto locationDto) {
        return new Location(id,
                            locationDto.getLat(),
                            locationDto.getLon());
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(),
                               location.getLon());
    }
}
