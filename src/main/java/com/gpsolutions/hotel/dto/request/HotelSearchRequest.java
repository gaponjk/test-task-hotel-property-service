package com.gpsolutions.hotel.dto.request;

import java.util.List;

public record HotelSearchRequest(
    String name, String brand, String city, String country, List<String> amenities) {}
