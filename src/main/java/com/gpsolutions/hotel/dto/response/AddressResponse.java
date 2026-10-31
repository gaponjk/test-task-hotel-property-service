package com.gpsolutions.hotel.dto.response;

public record AddressResponse(
        Integer houseNumber,
        String street,
        String city,
        String country,
        String postCode
) {
}