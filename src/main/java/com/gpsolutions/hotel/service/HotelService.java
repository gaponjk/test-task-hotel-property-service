package com.gpsolutions.hotel.service;

import com.gpsolutions.hotel.dto.request.CreateHotelRequest;
import com.gpsolutions.hotel.dto.request.HotelSearchRequest;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;

import java.util.List;
import java.util.Map;

public interface HotelService {
    List<HotelShortResponse> getAllHotels();

    HotelDetailsResponse getHotelById(Long id);

    List<HotelShortResponse> searchHotels(HotelSearchRequest hotelSearchRequest);

    HotelShortResponse createHotel(CreateHotelRequest createHotelRequest);

    void addAmenities(Long id, List<String> amenities);

    Map<String, Long> getHistogram(String param);
}
