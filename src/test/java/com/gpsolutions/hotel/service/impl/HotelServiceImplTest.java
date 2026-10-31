package com.gpsolutions.hotel.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.gpsolutions.hotel.dto.request.*;
import com.gpsolutions.hotel.dto.response.AddressResponse;
import com.gpsolutions.hotel.dto.response.ArrivalTimeResponse;
import com.gpsolutions.hotel.dto.response.ContactsResponse;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;
import com.gpsolutions.hotel.entity.Amenity;
import com.gpsolutions.hotel.entity.Hotel;
import com.gpsolutions.hotel.exception.BadRequestException;
import com.gpsolutions.hotel.exception.NotFoundException;
import com.gpsolutions.hotel.mapper.HotelMapper;
import com.gpsolutions.hotel.repository.AmenityRepository;
import com.gpsolutions.hotel.repository.HotelRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

  @Mock private HotelRepository hotelRepository;

  @Mock private AmenityRepository amenityRepository;

  @Mock private HotelMapper hotelMapper;

  @InjectMocks private HotelServiceImpl hotelService;

  @Test
  @DisplayName("getAllHotels: should return list of mapped HotelShortResponse")
  void getAllHotels_shouldReturnListOfHotels() {
    // given
    Hotel hotel = new Hotel();
    hotel.setId(1L);
    HotelShortResponse shortResponse =
        new HotelShortResponse(1L, "DoubleTree", "Desc", "Address", "+375170000000");

    given(hotelRepository.findAll()).willReturn(List.of(hotel));
    given(hotelMapper.toShortResponse(hotel)).willReturn(shortResponse);

    // when
    List<HotelShortResponse> result = hotelService.getAllHotels();

    // then
    assertThat(result).hasSize(1).containsExactly(shortResponse);
    verify(hotelRepository).findAll();
    verify(hotelMapper).toShortResponse(hotel);
  }

  @Test
  @DisplayName("getHotelById: when hotel exists should return HotelDetailsResponse")
  void getHotelById_whenHotelExists_shouldReturnDetailsResponse() {
    // given
    Long hotelId = 1L;
    Hotel hotel = new Hotel();
    hotel.setId(hotelId);

    HotelDetailsResponse detailsResponse =
        new HotelDetailsResponse(
            hotelId,
            "DoubleTree",
            "Desc",
            "Hilton",
            new AddressResponse(9, "Street", "City", "Country", "220004"),
            new ContactsResponse("+375170000000", "test@test.com"),
            new ArrivalTimeResponse("14:00", "12:00"),
            List.of("Free WiFi"));

    given(hotelRepository.findDetailedById(hotelId)).willReturn(Optional.of(hotel));
    given(hotelMapper.toDetailsResponse(hotel)).willReturn(detailsResponse);

    // when
    HotelDetailsResponse result = hotelService.getHotelById(hotelId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(hotelId);
    assertThat(result.name()).isEqualTo("DoubleTree");
    verify(hotelRepository).findDetailedById(hotelId);
    verify(hotelMapper).toDetailsResponse(hotel);
  }

  @Test
  @DisplayName("getHotelById: when hotel not found should throw NotFoundException")
  void getHotelById_whenHotelDoesNotExist_shouldThrowNotFoundException() {
    // given
    Long nonExistentId = 999L;
    given(hotelRepository.findDetailedById(nonExistentId)).willReturn(Optional.empty());

    // when
    Throwable thrown = catchThrowable(() -> hotelService.getHotelById(nonExistentId));

    // then
    assertThat(thrown)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Hotel not found with id: " + nonExistentId);

    verify(hotelRepository).findDetailedById(nonExistentId);
    verify(hotelMapper, never()).toDetailsResponse(any());
  }

  @Test
  @DisplayName("searchHotels: should query repository with Specification and map results")
  void searchHotels_whenCriteriaProvided_shouldReturnMatchingHotels() {
    // given
    HotelSearchRequest criteria = new HotelSearchRequest("Hilton", null, "Minsk", null, null);
    Hotel hotel = new Hotel();
    hotel.setId(1L);
    HotelShortResponse shortResponse =
        new HotelShortResponse(1L, "Hilton", "Desc", "Address", "+375170000000");

    given(hotelRepository.findAll(ArgumentMatchers.<Specification<Hotel>>any()))
        .willReturn(List.of(hotel));
    given(hotelMapper.toShortResponse(hotel)).willReturn(shortResponse);

    // when
    List<HotelShortResponse> result = hotelService.searchHotels(criteria);

    // then
    assertThat(result).hasSize(1).containsExactly(shortResponse);
    verify(hotelRepository).findAll(ArgumentMatchers.<Specification<Hotel>>any());
    verify(hotelMapper).toShortResponse(hotel);
  }

  @Test
  @DisplayName("createHotel: should map request, save entity and return short response")
  void createHotel_whenValidRequest_shouldSaveAndReturnShortResponse() {
    // given
    CreateHotelRequest request =
        new CreateHotelRequest(
            "New Hotel",
            "Desc",
            "Brand",
            new AddressRequest(1, "Street", "City", "Country", "100000"),
            new ContactsRequest("+375170000000", "test@test.com"),
            new ArrivalTimeRequest("14:00", "12:00"));

    Hotel hotelToSave = new Hotel();
    hotelToSave.setName("New Hotel");

    Hotel savedHotel = new Hotel();
    savedHotel.setId(10L);
    savedHotel.setName("New Hotel");

    HotelShortResponse response =
        new HotelShortResponse(10L, "New Hotel", "Desc", "Address", "+375170000000");

    given(hotelMapper.toEntity(request)).willReturn(hotelToSave);
    given(hotelRepository.save(hotelToSave)).willReturn(savedHotel);
    given(hotelMapper.toShortResponse(savedHotel)).willReturn(response);

    // when
    HotelShortResponse result = hotelService.createHotel(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(10L);
    assertThat(result.name()).isEqualTo("New Hotel");
    verify(hotelMapper).toEntity(request);
    verify(hotelRepository).save(hotelToSave);
    verify(hotelMapper).toShortResponse(savedHotel);
  }

  @Test
  @DisplayName("addAmenities: should persist only new amenities and attach all to hotel")
  void addAmenities_whenNewAndExistingAmenities_shouldLinkAndSave() {
    // given
    Long hotelId = 1L;
    Hotel hotel = new Hotel();
    hotel.setId(hotelId);
    hotel.setAmenities(new HashSet<>());

    Amenity existingAmenity = new Amenity();
    existingAmenity.setId(100L);
    existingAmenity.setName("Free WiFi");

    List<String> inputAmenities = List.of("Free WiFi", "Free parking", "  ");

    given(hotelRepository.findDetailedById(hotelId)).willReturn(Optional.of(hotel));
    given(amenityRepository.findByNameIn(Set.of("Free WiFi", "Free parking")))
        .willReturn(new HashSet<>(List.of(existingAmenity)));

    given(
            amenityRepository.saveAll(
                argThat(
                    iterable -> {
                      List<Amenity> list = new ArrayList<>();
                      iterable.forEach(list::add);
                      return list.size() == 1 && list.getFirst().getName().equals("Free parking");
                    })))
        .willAnswer(
            invocation -> {
              Amenity newAmenity = new Amenity();
              newAmenity.setId(101L);
              newAmenity.setName("Free parking");
              return List.of(newAmenity);
            });

    // when
    hotelService.addAmenities(hotelId, inputAmenities);

    // then
    assertThat(hotel.getAmenities())
        .hasSize(2)
        .extracting(Amenity::getName)
        .containsExactlyInAnyOrder("Free WiFi", "Free parking");

    verify(hotelRepository).findDetailedById(hotelId);
    verify(amenityRepository).findByNameIn(Set.of("Free WiFi", "Free parking"));
    verify(amenityRepository).saveAll(any());
    verify(hotelRepository).save(hotel);
  }

  @Test
  @DisplayName("addAmenities: when hotel does not exist should throw NotFoundException")
  void addAmenities_whenHotelNotFound_shouldThrowNotFoundException() {
    // given
    Long nonExistentId = 999L;
    given(hotelRepository.findDetailedById(nonExistentId)).willReturn(Optional.empty());

    // when
    Throwable thrown =
        catchThrowable(() -> hotelService.addAmenities(nonExistentId, List.of("Free WiFi")));

    // then
    assertThat(thrown)
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Hotel not found with id: " + nonExistentId);

    verify(hotelRepository).findDetailedById(nonExistentId);
    verify(amenityRepository, never()).findByNameIn(any());
    verify(hotelRepository, never()).save(any());
  }

  @Test
  @DisplayName("addAmenities: when amenity list is empty should not query amenity repository")
  void addAmenities_whenListIsEmpty_shouldDoNothing() {
    // given
    Long hotelId = 1L;
    Hotel hotel = new Hotel();
    hotel.setId(hotelId);
    given(hotelRepository.findDetailedById(hotelId)).willReturn(Optional.of(hotel));

    // when
    hotelService.addAmenities(hotelId, Collections.emptyList());

    // then
    verify(hotelRepository).findDetailedById(hotelId);
    verify(amenityRepository, never()).findByNameIn(any());
    verify(hotelRepository, never()).save(any());
  }

  @Test
  @DisplayName("getHistogram: should call repository for 'city' parameter case-insensitively")
  void getHistogram_whenParamCity_shouldReturnAggregatedMap() {
    // given
    List<Object[]> rawCounts = new ArrayList<>();
    rawCounts.add(new Object[] {"Minsk", 2L});
    rawCounts.add(new Object[] {"Grodno", 1L});

    given(hotelRepository.countHotelsByCity()).willReturn(rawCounts);

    // when
    Map<String, Long> histogram = hotelService.getHistogram("  CiTy  ");

    // then
    assertThat(histogram).hasSize(2).containsEntry("Minsk", 2L).containsEntry("Grodno", 1L);
    verify(hotelRepository).countHotelsByCity();
  }

  @Test
  @DisplayName("getHistogram: when parameter is invalid should throw BadRequestException")
  void getHistogram_whenInvalidParam_shouldThrowBadRequestException() {
    // given
    String invalidParam = "unsupported_field";

    // when
    Throwable thrown = catchThrowable(() -> hotelService.getHistogram(invalidParam));

    // then
    assertThat(thrown)
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Invalid histogram parameter: 'unsupported_field'");
  }

  @Test
  @DisplayName("getHistogram: when parameter is blank should throw BadRequestException")
  void getHistogram_whenBlankParam_shouldThrowBadRequestException() {
    // given
    String blankParam = "   ";

    // when
    Throwable thrown = catchThrowable(() -> hotelService.getHistogram(blankParam));

    // then
    assertThat(thrown)
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Histogram parameter cannot be empty");
  }
}
