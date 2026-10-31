package com.gpsolutions.hotel.controller;

import com.gpsolutions.hotel.dto.request.CreateHotelRequest;
import com.gpsolutions.hotel.dto.request.HotelSearchRequest;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;
import com.gpsolutions.hotel.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // 1. GET /property-view/hotels
    @GetMapping("/hotels")
    public List<HotelShortResponse> getAllHotels() {
        return hotelService.getAllHotels();
    }

    // 2. GET /property-view/hotels/{id}
    @GetMapping("/hotels/{id}")
    public HotelDetailsResponse getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }

    // 3. GET /property-view/search?name=...
    @GetMapping("/search")
    public List<HotelShortResponse> searchHotels(@ModelAttribute HotelSearchRequest request) {
        return hotelService.searchHotels(request);
    }

    // 4. POST /property-view/hotels
    @PostMapping("/hotels")
    @ResponseStatus(HttpStatus.CREATED)
    public HotelShortResponse createHotel(@Valid @RequestBody CreateHotelRequest request) {
        return hotelService.createHotel(request);
    }

    // 5. POST /property-view/hotels/{id}/amenities
    @PostMapping("/hotels/{id}/amenities")
    @ResponseStatus(HttpStatus.OK)
    public void addAmenities(@PathVariable Long id, @RequestBody List<String> amenities) {
        hotelService.addAmenities(id, amenities);
    }

    // 6. GET /property-view/histogram/{param}
    @GetMapping("/histogram/{param}")
    public Map<String, Long> getHistogram(@PathVariable String param) {
        return hotelService.getHistogram(param);
    }
}