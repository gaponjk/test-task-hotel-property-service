package com.gpsolutions.hotel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.gpsolutions.hotel.dto.request.AddressRequest;
import com.gpsolutions.hotel.dto.request.ArrivalTimeRequest;
import com.gpsolutions.hotel.dto.request.ContactsRequest;
import com.gpsolutions.hotel.dto.request.CreateHotelRequest;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;
import com.gpsolutions.hotel.entity.Address;
import com.gpsolutions.hotel.entity.Amenity;
import com.gpsolutions.hotel.entity.ArrivalTime;
import com.gpsolutions.hotel.entity.Contact;
import com.gpsolutions.hotel.entity.Hotel;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HotelMapperTest {

  private HotelMapper hotelMapper;

  @BeforeEach
  void setUp() {
    hotelMapper = new HotelMapper();
  }

  @Test
  @DisplayName("toShortResponse: should format address string and extract phone to root")
  void toShortResponse_whenValidHotel_shouldReturnShortResponseWithFormattedAddressAndPhone() {
    // given
    Hotel hotel = createDefaultHotel();

    // when
    HotelShortResponse response = hotelMapper.toShortResponse(hotel);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.name()).isEqualTo("DoubleTree by Hilton Minsk");
    assertThat(response.description()).isEqualTo("Luxurious hotel in Minsk");
    assertThat(response.address()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
    assertThat(response.phone()).isEqualTo("+375 17 309-80-00");
  }

  @Test
  @DisplayName("toDetailsResponse: should correctly map nested objects and flatten amenities")
  void toDetailsResponse_whenValidHotel_shouldReturnDetailsResponseWithFlattenedAmenities() {
    // given
    Hotel hotel = createDefaultHotel();

    // when
    HotelDetailsResponse response = hotelMapper.toDetailsResponse(hotel);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.name()).isEqualTo("DoubleTree by Hilton Minsk");
    assertThat(response.brand()).isEqualTo("Hilton");

    assertThat(response.address()).isNotNull();
    assertThat(response.address().street()).isEqualTo("Pobediteley Avenue");
    assertThat(response.address().city()).isEqualTo("Minsk");

    assertThat(response.contacts()).isNotNull();
    assertThat(response.contacts().phone()).isEqualTo("+375 17 309-80-00");
    assertThat(response.contacts().email()).isEqualTo("info@hilton.com");

    assertThat(response.arrivalTime()).isNotNull();
    assertThat(response.arrivalTime().checkIn()).isEqualTo("14:00");
    assertThat(response.arrivalTime().checkOut()).isEqualTo("12:00");

    assertThat(response.amenities())
        .hasSize(2)
        .containsExactlyInAnyOrder("Free WiFi", "Free parking");
  }

  @Test
  @DisplayName("toEntity: should map request to entity and establish bidirectional relations")
  void toEntity_whenValidCreateRequest_shouldReturnHotelWithBiDirectionalRelations() {
    // given
    CreateHotelRequest request = createValidCreateHotelRequest();

    // when
    Hotel hotel = hotelMapper.toEntity(request);

    // then
    assertThat(hotel).isNotNull();
    assertThat(hotel.getName()).isEqualTo("DoubleTree by Hilton Minsk");
    assertThat(hotel.getBrand()).isEqualTo("Hilton");
    assertThat(hotel.getDescription()).isEqualTo("Hotel description");

    assertThat(hotel.getAddress()).isNotNull();
    assertThat(hotel.getAddress().getCity()).isEqualTo("Minsk");
    assertThat(hotel.getAddress().getHotel()).isSameAs(hotel);

    assertThat(hotel.getContact()).isNotNull();
    assertThat(hotel.getContact().getEmail()).isEqualTo("info@hilton.com");
    assertThat(hotel.getContact().getHotel()).isSameAs(hotel);

    assertThat(hotel.getArrivalTime()).isNotNull();
    assertThat(hotel.getArrivalTime().getCheckIn()).isEqualTo("14:00");
    assertThat(hotel.getArrivalTime().getHotel()).isSameAs(hotel);

    assertThat(hotel.getAmenities()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("null handling: should return null when source entity or request is null")
  void mapping_whenSourceIsNull_shouldReturnNull() {
    // given
    Hotel nullHotel = null;
    CreateHotelRequest nullRequest = null;

    // when
    HotelShortResponse shortResponse = hotelMapper.toShortResponse(nullHotel);
    HotelDetailsResponse detailsResponse = hotelMapper.toDetailsResponse(nullHotel);
    Hotel entity = hotelMapper.toEntity(nullRequest);

    // then
    assertThat(shortResponse).isNull();
    assertThat(detailsResponse).isNull();
    assertThat(entity).isNull();
  }

  // methods for code to be cleaner
  private Hotel createDefaultHotel() {
    Hotel hotel = new Hotel();
    hotel.setId(1L);
    hotel.setName("DoubleTree by Hilton Minsk");
    hotel.setDescription("Luxurious hotel in Minsk");
    hotel.setBrand("Hilton");

    Address address = new Address();
    address.setHouseNumber(9);
    address.setStreet("Pobediteley Avenue");
    address.setCity("Minsk");
    address.setPostCode("220004");
    address.setCountry("Belarus");
    hotel.setAddress(address);

    Contact contact = new Contact();
    contact.setPhone("+375 17 309-80-00");
    contact.setEmail("info@hilton.com");
    hotel.setContact(contact);

    ArrivalTime arrivalTime = new ArrivalTime();
    arrivalTime.setCheckIn("14:00");
    arrivalTime.setCheckOut("12:00");
    hotel.setArrivalTime(arrivalTime);

    Amenity wifi = new Amenity();
    wifi.setName("Free WiFi");
    Amenity parking = new Amenity();
    parking.setName("Free parking");
    hotel.setAmenities(Set.of(wifi, parking));

    return hotel;
  }

  private CreateHotelRequest createValidCreateHotelRequest() {
    AddressRequest addressRequest =
        new AddressRequest(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004");
    ContactsRequest contactsRequest = new ContactsRequest("+375 17 309-80-00", "info@hilton.com");
    ArrivalTimeRequest arrivalTimeRequest = new ArrivalTimeRequest("14:00", "12:00");

    return new CreateHotelRequest(
        "DoubleTree by Hilton Minsk",
        "Hotel description",
        "Hilton",
        addressRequest,
        contactsRequest,
        arrivalTimeRequest);
  }
}
