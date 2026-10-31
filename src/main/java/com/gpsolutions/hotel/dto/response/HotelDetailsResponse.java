package com.gpsolutions.hotel.dto.response;

import java.util.List;

public record HotelDetailsResponse(
        Long id,
        String name,
        String description,
        String brand,
        AddressResponse address,
        ContactsResponse contacts,
        ArrivalTimeResponse arrivalTime,
        List<String> amenities
) {
}