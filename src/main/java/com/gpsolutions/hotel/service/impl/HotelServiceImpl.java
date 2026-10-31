package com.gpsolutions.hotel.service.impl;

import com.gpsolutions.hotel.dto.request.CreateHotelRequest;
import com.gpsolutions.hotel.dto.request.HotelSearchRequest;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;
import com.gpsolutions.hotel.entity.Amenity;
import com.gpsolutions.hotel.entity.Hotel;
import com.gpsolutions.hotel.exception.BadRequestException;
import com.gpsolutions.hotel.exception.NotFoundException;
import com.gpsolutions.hotel.mapper.HotelMapper;
import com.gpsolutions.hotel.repository.AmenityRepository;
import com.gpsolutions.hotel.repository.HotelRepository;
import com.gpsolutions.hotel.repository.specification.HotelSpecification;
import com.gpsolutions.hotel.service.HotelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            AmenityRepository amenityRepository,
                            HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.amenityRepository = amenityRepository;
        this.hotelMapper = hotelMapper;
    }

    @Override
    public List<HotelShortResponse> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(hotelMapper::toShortResponse)
                .toList();
    }

    @Override
    public HotelDetailsResponse getHotelById(Long id) {
        return hotelRepository.findDetailedById(id)
                .map(hotelMapper::toDetailsResponse)
                .orElseThrow(() -> new NotFoundException("Hotel not found with id: " + id));
    }

    @Override
    public List<HotelShortResponse> searchHotels(HotelSearchRequest criteria) {
        return hotelRepository.findAll(HotelSpecification.filterBy(criteria)).stream()
                .map(hotelMapper::toShortResponse)
                .toList();
    }

    @Override
    @Transactional
    public HotelShortResponse createHotel(CreateHotelRequest request) {
        Hotel hotel = hotelMapper.toEntity(request);
        Hotel savedHotel = hotelRepository.save(hotel);
        return hotelMapper.toShortResponse(savedHotel);
    }

    @Override
    @Transactional
    public void addAmenities(Long id, List<String> amenityNames) {
        Hotel hotel = hotelRepository.findDetailedById(id)
                .orElseThrow(() -> new NotFoundException("Hotel not found with id: " + id));

        if (amenityNames == null || amenityNames.isEmpty()) {
            return;
        }

        Set<String> cleanNames = amenityNames.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        if (cleanNames.isEmpty()) {
            return;
        }

        Set<Amenity> existingAmenities = amenityRepository.findByNameIn(cleanNames);
        Set<String> existingNames = existingAmenities.stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());

        List<Amenity> newAmenities = cleanNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> {
                    Amenity amenity = new Amenity();
                    amenity.setName(name);
                    return amenity;
                })
                .toList();

        if (!newAmenities.isEmpty()) {
            existingAmenities.addAll(amenityRepository.saveAll(newAmenities));
        }

        if (hotel.getAmenities() == null) {
            hotel.setAmenities(new HashSet<>());
        }
        hotel.getAmenities().addAll(existingAmenities);

        hotelRepository.save(hotel);
    }

    @Override
    public Map<String, Long> getHistogram(String param) {
        if (param == null || param.isBlank()) {
            throw new BadRequestException("Histogram parameter cannot be empty");
        }

        List<Object[]> rawResults = switch (param.strip().toLowerCase()) {
            case "brand" -> hotelRepository.countHotelsByBrand();
            case "city" -> hotelRepository.countHotelsByCity();
            case "country" -> hotelRepository.countHotelsByCountry();
            case "amenities" -> hotelRepository.countHotelsByAmenity();
            default -> throw new BadRequestException(
                    "Invalid histogram parameter: '" + param + "'. Allowed values: brand, city, country, amenities"
            );
        };

        return toHistogramMap(rawResults);
    }

    private Map<String, Long> toHistogramMap(List<Object[]> rawResults) {
        if (rawResults == null || rawResults.isEmpty()) {
            return Collections.emptyMap();
        }

        return rawResults.stream()
                .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }
}