package com.gpsolutions.hotel.repository.specification;

import java.util.List;
//Just for Specification
public record HotelSearchCriteria (
        String name,
        String brand,
        String city,
        String country,
        List<String> amenities
){
}
